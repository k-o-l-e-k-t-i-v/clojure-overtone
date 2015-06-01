;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone
(def cbus (control-bus))
(def cbus-2 (control-bus))

(defsynth cmetro
  [rate  1
   outb  0
   minim 1
   maxim 32]
  (let [trig (impulse:kr rate)
        step (stepper:kr trig :min minim :max maxim)]
    (send-trig:kr trig step)
    (out:kr outb trig)))

(defsynth cmetro-chaos
  [rate  1
   outb  0
   minim 1
   maxim 32]
  (let [trig (impulse:kr (+ rate (* 0.5 (* rate (lf-noise0:kr rate)))))
        step (stepper:kr trig :min minim :max maxim)]
    (send-trig:kr trig step)
    (out:kr outb trig)))

(def t1 (cmetro :outb cbus))
(def tc1 (cmetro-chaos :outb cbus :rate 2))
(def t2 (cmetro :outb cbus-2 :rate 2))
(def tc2 (cmetro-chaos :outb cbus-2 :rate 5))

(ctl t1 :rate 12)
(ctl t2 :rate 12)

(kill cmetro-chaos)

(definst bkik
  [freq 30
   facc 300
   ft   0.1
   at   0.2
   tbus 0
   div 1]
  (let [trig (pulse-divider:kr (in:kr tbus) div)
        fenv (+ freq (* facc (decay trig ft)))
        aenv (decay trig at)
        src  (sin-osc fenv)
        sig  (* src aenv)]
    (out 0 (pan2 sig 0 1))))


(def k1 (bkik :tbus cbus))
(ctl k1 :facc 200 :at 0.6 :freq 30)

(kill cmetro)

(def k2 (bkik :tbus cbus-2 :freq 80 :at 0.08 :ft 0.02))
(ctl k1 :div 3)
(kill bkik)

(do
  (ctl k1 :div 8)
  (ctl k2 :div 6)
  (ctl t1 :rate 12)
  (ctl t2 :rate 12))

(definst hat
  [freq 2000
   amp 0.1
   facc 1000
   ft 0.1
   at 0.15
   tbus 0
   div 1]
  (let [trig (pulse-divider:kr (in:kr tbus) div)
        fenv (+ freq (* facc (decay trig ft)))
        aenv (* amp (decay trig at))
        src  (lf-noise0:ar fenv)
        flt  (rhpf:ar src fenv 0.9)
        sig  (* flt aenv)
        sig  (free-verb sig 0.8 0.4 0.7)]
    (out 0 (pan2 sig))))

(def h1 (hat :tbus cbus :div 2))
(def h2 (hat :tbus cbus-2 :div 5))
(def h3 (hat :tbus cbus-2 :div 7))
(def h4 (hat :tbus cbus :div 8 :freq 600 :facc 500 :amp 0.8))
(def h5 (hat :tbus cbus :div 10 :freq 1600 :facc 1500 :amp 0.98))
(def h6 (hat :tbus cbus :div 7 :freq 900 :facc 300 :amp 0.8))
(def h7 (hat :tbus cbus :div 5 :freq 700 :facc 700 :amp 0.8))
(do (ctl h3 :div 2))
(kill hat)

(defsynth glitcher
  [tbus  0
   div   6
   mult  2
   del   1
   len   1
   maxi  3
   amp   0.5]
  (let [trig  (pulse-divider:kr (in:kr tbus) div)
        sigin (in:ar 0 1)
        glit  (allpass-c:ar sigin maxi del len)
        aenv  (* amp (decay trig len))
        sig   (* glit aenv)]
    (out 0 (pan2 sig 0 1))))

(def g4 (glitcher :tbus cbus :len 5.48 :del 0.13 :div 28))
(ctl g4 :del 12/36)

(ctl g1 :div 16 :del 0.025 :amp 0.9 :len 1.2)




(demo 3 (replace-out:ar [0 1] (freq-shift (in:kr 0 2) 200)))
(definst feed
  [amp   1
   shift 0
   phase 0
   bus   0
   ch    1]
  (let [inlet  (in:ar bus 2)]
    (replace-out:ar inlet (* amp (freq-shift inlet shift phase)))
    ))

(def f (feed :shift 150 :bus 0 :amp 0.3))
(kill feed)
(demo 30 (sin-osc 200))
(definst rak
  [t 20
   f 2000
   fm 300
   ff 300]
  (let [env (* (decay (dust 4) 0.3) (env-gen (perc (* 0.5 t) (* 0.5 t) 1 -10) 1 1 0 1))
        fmod (+ f (* fm (decay (dust 2) 0.2)))
        sig (+ (blip (* 0.2 fmod) (* 20 env)) (saw (* 0.9953 fmod)) (sin-osc-fb f env) (square (+ (* f env) fmod)))
        sig (rlpf sig fmod 1.9)
        sig (* sig env)
        sig (free-verb sig 0.299 0.97 0.185)
        sil (detect-silence sig 0.0001 2 FREE)
        ]
    (out 0 (pan2 sig 0 1))))

(def rak1 (rak))
(def rak1 (rak :t 20 :f 300 :fm 280))

(definst melo
  [nbuf 0
   dur  1
   tbus 0
   div  1]
  (let [trig (pulse-divider:kr (in:kr tbus) div)
        step (stepper:kr trig :min 0 :max 15)
        note (buf-rd:kr 1 nbuf step)
        freq (midicps note)
        src  (sin-osc freq)
        amp  (decay trig dur)
        outs  (* amp src)
        outs (free-verb (freq-shift outs 200) 0.3 0.8 0.4)]
    (out 0 (pan2 outs 0))))


(def notes (buffer 16))
(def notes2 (buffer 16))
(buffer-write! notes (take 16 (cycle (interleave (shuffle (chord :C5 :7)) (shuffle (chord :d3 :minor7))))))
(buffer-write! notes (take 16 (cycle (map note [:c4 :c4 :d4 :f4 :f#4]))))
(buffer-write! notes2 (take 16 (cycle (map note [:c4 :c3 :d3 :f4 :f#4 :a4]))))

(def m2 (melo :tbus cbus :div 3 :nbuf notes))
(def m3 (melo :tbus cbus :div 5 :nbuf notes2))
(def m4 (melo :tbus cbus :div 7 :nbuf notes2))

(ctl t1 :rate 0.3)
(kill melo)
(kill cmetro)
(stop)

(ctl m1 :note (choose (shuffle (chord :C2 :dim7 3))))
