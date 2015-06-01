;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone




(def PORT 7777)
PORT

(def server (osc-server PORT))

(osc-handle server "/bang"  )


(osc-listen server (kokora) :bang)

(osc-listeners server)

(osc-rm-listener server :bang)

(osc-handlers server)

(osc-rm-handler server "/bang")

(defn kokora
  []
  (demo 4 (out 0 (pan2 (rlpf (comb-c (* (env-gen (perc 0 0.005 10)
                                                 1 0.81 0 1 )
                                         (white-noise))
                                     4
                                     (+ 0.021
                                        (* 0.02
                                           (lf-noise2:ar (rand 20) )))
                                     0.92)
                             (+ (* 1800
                                   (lf-noise2:ar 2))
                                2000)
                             0.92)))))


(kokora)

(demo 60
      (out 0
           (pan2  (normalizer (comb-c (free-verb (normalizer (+
                                                               (distort  (* 30 (comb-c (rlpf (comb-c (* (env-gen (perc 0 0.5 10)
                                                                                                                 1 0.81 0 1 )
                                                                                                        (formant:ar 100 1 30))
                                                                                                     4
                                                                                                     (+ 0.41
                                                                                                        (* 0.4
                                                                                                           (lf-noise2:ar 8 )))
                                                                                                     2.999992)
                                                                                             (+ (* 280
                                                                                                   (lf-noise2:ar 20))
                                                                                                400)
                                                                                             0.2)
                                                                                       2
                                                                                       (+ 0.41
                                                                                          (* 0.4
                                                                                             (lf-noise2:ar 4)))
                                                                                       0.9)))
                                                               (comb-c (rlpf (comb-c (* (env-gen (perc 0 0.005 10)
                                                                                                 1 0.81 0 1 )
                                                                                        (saw 300))
                                                                                     4
                                                                                     (+ 0.41
                                                                                        (* 0.4
                                                                                           (lf-noise2:ar 8 )))
                                                                                     2.999992)
                                                                             (+ (* 280
                                                                                   (lf-noise2:ar 20))
                                                                                400)
                                                                             0.2)
                                                                       2
                                                                       (+ 0.41
                                                                          (* 0.4
                                                                             (lf-noise2:ar 8)))
                                                                       0.9)))
                                                 0.3 0.9 0.2)
                                      4 0.03 0.9))
                  (lf-noise2:ar 3))))
(stop)



(kill-server)
(boot-external-server)








(demo 60
      (out 0 (pan2 (*  (free-verb  (normalizer  (+  (* (pow  (lf-noise2:ar 200) 4)
                                                       (resonz (apply +
                                                                       (* (sin-osc (+ 40 (rand 200)))
                                                                          (sin-osc (+ (into [] (repeatedly 20 (fn [] (+ 100 (rand 3000)))))
                                                                                      (* 120
                                                                                         (sin-osc (rand 300)))))))
                                                               400 0.9))
                                                    ;(sin-osc (+ 80 (rand 1200)))
                                                    ;(sin-osc (+ 90 (rand 2000)))
                                                    ))
                                   0.5 0.8 0.1)
                       (env-gen (envelope [ 0 1 0 ] [ 10 40 10]) 1 1 0 1))
                   (lf-noise2:ar 2)
                   0.315)))
(stop)

(def cb1 (control-bus 1))

(definst metracek
  [rate 16
   outb 0]
  (out outb (impulse:kr rate)))

(def m1 (metracek :rate 16 :outb cb1))
(kill m1)
(ctl m1 :rate 32)
(demo 1 (out 0 (pan2 (comb-c  (* (decay (pulse-divider:kr (in:kr cb1) 4) 0.02)
                                 (white-noise))
                              2 0.02 0.9))))
(stop)

(definst hatka
  [cbus 0
   div  4
   d    0.02
   bfr  0]
  (let [t (pulse-divider:kr (in:kr cbus) div)
        n (stepper:kr t :min 0 :max 16 )
        g (buf-rd:kr 1 bfr n)
        e (decay g 0.02)
        s (* e (white-noise))
        s (comb-c s 1 d 0.2)]
    (out 0 (pan2 s (lf-noise2:ar 2) 0.5))))

(stepp)









(definst brada
  [freq 80
   fmod 2000
   cbus 0
   div  4
   cd    0.02
   cdec  0.9
   bfr   0]
  (let [t (pulse-divider:kr (in:kr cbus) div)
        n (stepper:kr t :min 0 :max 16 )
        g (buf-rd:kr 1 bfr n)
        e (env-gen (perc 0 0.2 10) g 1 0 1)
        fe  (env-gen (perc 0 0.02 -10) g fmod freq 1)
        fe2 (env-gen (perc 0.04 -5) g 3 1 1)
        s (* e (sin-osc-fb:ar fe 0.3))
        s (comb-c s 1 cd cdec)
        s (normalizer (distort (* 20 s)))]
    (out 0 (pan2 s (lf-noise2:ar 2) 0.5))))

(kill hatka)
(def h1 (hatka :cbus cb1 :div 6 :4 0.012 :bfr hatpatbuff))
(def h2 (hatka :cbus cb1 :div 4 :d 0.03 :bfr hatpatbuff))
(def h3 (hatka :cbus cb1 :div 8 :d 0.08 :bfr hatpatbuff))
(def h4 (hatka :cbus cb1 :div 12 :d 0.1 :bfr hatpatbuff))
(do
  (def h5 (hatka :cbus cb1 :div 4 :d 0.2 :bfr hatpatbuff))
  (def h5 (hatka :cbus cb1 :div 6 :d 0.3 :bfr hatpatbuff)))
(ctl h5 :cd 0.003 :cdec 0.9)
(ctl h5 :div 4)
(def b1 (brada :cbus cb1 :div 8 :cd 0.03 :bfr bpatbuff))
(def b3 (brada :cbus cb1 :div 12 :cd 0.3 :freq 20))
(def b3 (brada :cbus cb1 :div 16 :cd 0.14 :freq 80))
(ctl b3 :freq 90 :div 20 :cd 0.8 :cdec 1.4)
(ctl b2 :div 12)
(ctl b3 :div 16)
(kill hatka)
(kill brada)
(kill b3)
(definst bd
  [])


;(ctl b1 :cd 0.03 :cdec 0.06)
(kill hatka)
(kill brada)

(def hatpat [1 1 1 0
             1 1 1 0
             1 1 1 0
             1 0 1 0])

(def bpat [1 0 0 0
           1 0 0 1
           0 1 0 0
           1 0 1 0])


(def bpatbuff (buffer 16 1))
(def hatpatbuff (buffer 16 1))

(buffer-write! bpatbuff bpat)
(buffer-write! hatpatbuff hatpat)
