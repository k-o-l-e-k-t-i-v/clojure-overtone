;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone


(stop)
(demo 400 (let [s (fn [f] (* (pow (lf-noise2:ar 3) 2) (saw:ar f)))
                c (apply + (map s (into [] (repeatedly 10 (fn [] (+ 640/7 (rand 10)))))))
                a (env-gen (envelope [0 1 1 0] [10 350 10]) :action FREE)
                c (* a c)
                c (free-verb c 0.9 0.9 0.2)
                c (normalizer c)
                c (* a c)
                ]
            (out 0 (pan2 c) )))

(demo 400 (let [s1 (env-gen (envelope [0 1 0] [200 200]))
                s2 (env-gen (envelope [1 0 1] [200 200]))
                i1 (in:ar 0 1)
                i2 (in:ar 1 1)
                o1 (* i1 s2)
                o2 (* i2 s2)
                r1 (* i1 (* s1 (pulse:ar 3)))
                r2 (* i2 (* s1 (pulse:ar 4)))
                r1 (comb-c r1 2 0.041 0.9)
                r2 (comb-c r2 2 0.038 0.9)]
            (replace-out:ar 1 (+ o1 r1))
            (replace-out:ar 0 (+ o2 r2))))
(stop)

(defn plushalf [x]
  (+ x (int (/ x 2))))


(defn gentyp [x]
  (let [m (mod x 5)]
    (cond (= m 0) :bd
          (= m 1) :sd
          (= m 2) :bd
          (= m 3) :hh
          (= m 4) :sd)))

(defn gensek [seed count]
  (if (pos? count)
    (let [t (gentyp seed)
          seed-next (plushalf seed)]
      (cons t (gensek seed-next (dec count))))))

(def seq1 (into [] (gensek 7 32)))
(def seq2 (into [] (gensek 14 18)))
(def seq3 (into [] (gensek 430 7)))

(print seq1)

(definst bd
  [freq 40
   facc 5000
   ft   0.02
   amp  0.5
   at   0.82]
  (let [fe (env-gen (perc 0 ft facc -15) 1 1 freq 1)
        ae (env-gen (perc 0 at amp -5) 1 1 0 1 FREE)
        s (sin-osc-fb fe 0.18)
        s (distort (* (* 0.1 fe) s))
        sig-out (* s ae)
        p (lf-noise2:ar 0.2)]
    (out 0 (pan2 sig-out p))))


(definst sd
  [freq 4000
   facc -3800
   ft   0.2
   amp  0.95
   at   0.2]
  (let [fe (env-gen (perc 0 ft facc -5) 1 1 freq 1)
        ae (env-gen (perc 0 at amp -5) 1 1 0 1 FREE)
        s (+ (sin-osc-fb fe 3) (saw 4000) (white-noise))
        s1 (bpf s fe 4.9)
        s2 (rlpf s 200 0.2)
        s (normalizer (* s1 s2))
        s (distort (* (* 0.4 fe) s))
        sig-out (* s ae)
        p (lf-noise2:ar 0.2)]
    (out 0 (pan2 sig-out p))))

(definst hh
  [freq 4000
   facc -3800
   ft   0.2
   amp  0.95
   at   0.12]
  (let [fe (env-gen (perc 0 ft facc -5) 1 1 freq 1)
        ae (env-gen (perc 0 at amp -5) 1 1 0 1 FREE)
        s  (white-noise)
        s1 (bpf s fe 4.9)
        s2 (rhpf s 200 0.2)
        s (normalizer (* s1 s2))
        s (distort (* (* 0.4 fe) s))
        sig-out (* s ae)
        p (lf-noise2:ar 0.2)]
    (out 0 (pan2 sig-out p ))))
(kill-server
 )
(bd :facc 4000)
(sd :facc -1300 :freq 1380)
(hh :facc -3000 :freq 8000 :amp 0.9)

(defn pick-inst [key]
  (cond (= key :bd) (bd :facc 4000)
        (= key :sd) (sd :facc -1300 :freq 1380)
        (= key :hh) (hh :facc -3000 :freq 8000)))



(defn rotor [metro beat-n time insts]

  (let [
        t2 (+ beat-n time)
        i  (first insts)
        insts-rot (rotate 1 insts)]

    (at (metro beat-n) (pick-inst i))
    (apply-at (metro (+ t2)) rotor metro (+ beat-n time) time insts-rot [])
    ))

(def metro (metronome 400))
(metro-bpm metro 600)


(metro)

(rotor metro (metro) 4 seq1)
(rotor metro (metro) 6 seq2)
(rotor metro (metro) 1 seq3)

(stop)
(node-tree)

(def g1 (group "prefx" :head))
(def g2 (group "fx" :after g1))

                                        ;(inst-fx! bd fx-distortion-tubescreamer)




                                        ;(inst )
(def cbus1 (control-bus 1))
(definst mstclk
  [rate 32
   outb 0]
  (let [s (impulse:kr rate)]
    (out outb s)))
(def mclk1 (mstclk :rate 32 :outb cbus1))
(definst b
  [inb 0
   rbuf 0
   rbufdiv 0
   freq 80
   facc 1000
   tm 0.2
   of 0]
  (let [i (in:kr inb 1)
        r (stepper:kr i :min 0 :max 15)
        di (buf-rd:kr 1 rbufdiv r)
        t (pulse-divider:kr i di)
        p (stepper:kr t :min 0 :max 15)
        g (buf-rd:kr 1 rbuf p)
        a (decay (* g t) tm )
        f (env-gen (perc 0 0.02 facc) t 1 freq 1)
        s (square f)
        s (* a s)]
    (out 0 (pan2 s))))

(def b1 (b :inb cbus1))
(def b2 (b :inb cbus1 :di 6 :rbuf buf1))
(def b3 (b :inb cbus1 :di 2 :rbuf buf1))
(def b5 (b :inb cbus1 :rbuf buf2 :rbufdiv divbuf))
(kill b3)
(kill b2)

(def buf1 (buffer 16 1))
(def buf2 (buffer 12 1))

(buffer-write! buf1 [1 0 0 1
                     0 0 1 0
                     0 0 1 0
                     1 1 0 0])
(buffer-write! buf2 [1 0 0
                     1 0 1
                     0 1 0
                     0 1 0])
(def divbuf (buffer 16 1))
(buffer-write! divbuf [8 5 2 8
                       1 4 12 8
                       6 7 3 4
                       1 8 1 3])

(ctl b1 :freq 40)
(ctl mclk1 :rate 16)

(ctl b2 :rbuf buf2 :freq 220)

(demo 5 (let [i (in:ar 0 1)
              r (free-verb i 0.2 0.189 0.2)
              t (in:kr cbus1)
              f (stepper:kr t :min 0 :max 15)
              t (pulse-divider:kr t (* 2 (buf-rd:kr 1 divbuf f)))
              r (distort (* (* 20 (decay t 0.1)) r))
              r (normalizer r)]
           (replace-out:ar 0 r)
           (replace-out:ar 1 r)))

(ctl b1 :freq 300)
(ctl b2 :freq 320)
(ctl b3 :freq 500)
(ctl b4 :freq 600)
(ctl b5 :freq 900)

(ctl b :tm 2.95)
(stop)


(defn a [t 500]
  (apply-by))


(ctl b :freq 40)
(apply-by (+ (now) 5000) (ctl b :freq 50))
