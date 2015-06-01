;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone

(def kick-bus (buffer 32 1))
(def hihat-bus (buffer 16 1))

(buffer-write! kick-bus
              [1 0 0 0 0 0 0 0
               1 0 0 0 0 0 0 0
               1 0 0 0 0 0 0 0
               1 0 0 0 0 1 0 0])
(buffer-write! hihat-bus
               [1 0 0 0 0 0 0 0
                0 0 1 0 0 0 0 0])

(def cbus (control-bus))

(defsynth metro-synth
  [rate 1
   outbus 0]
  (let [t (impulse:kr rate)]
    (out outbus t)))
                                        ;(buff)
(def m1 (metro-synth :outbus cbus))
(ctl m1 :rate 4)

(definst kick
  [div    1
   inbus  0
   gbus   0
   outbus 0
   freq   30
   facc   200
   ft     0.03
   dur    0.2
   smin   1
   smax   8
   sval   1
   feed   0
   amp    0.5]
  (let [trgr  (pulse-divider:kr (in:kr inbus) div)
        step  (stepper:kr trgr :min (- smin 1) :max (- smax 1) :step sval)
        gates (buf-rd:kr 1 gbus step)
        aenv  (* gates amp (decay trgr dur))
        fenv  (+ freq (* facc (decay trgr ft)))
        src   (sin-osc-fb fenv feed)
        sig   (+ src (* 0.2 (comb-l:ar src 1 0.26 100)))
        sig   (* aenv sig)]
    (out 0 (pan2 sig 0 1))))

(def k1 (kick :inbus cbus :gbus kick-bus))
(def k2 (kick :inbus cbus :gbus kick-bus :facc 700 :smin 5 :smax 16))
(def k3 (kick :inbus cbus :gbus kick-bus :facc 700 :smin 17 :smax 29 :freq 80 ))
(def k4 (kick :inbus cbus :gbus kick-bus :facc 700 :smin 13 :smax 29 :freq 80 :feed 2))
(def k5 (kick :inbus cbus :gbus kick-bus :facc -700 :smin 13 :smax 25 :freq 900 :dur 0.1 :feed 2))
(def k6 (kick :inbus cbus :gbus kick-bus :facc -200 :smin 10 :smax 15 :freq 300 :dur 0.1 :feed 1.2))

(def h1 (kick :inbus cbus :gbus hihat-bus :facc -2000 :smin 1 :smax 16 :freq 3000 :dur 0.08 :feed 5.2))

(kill k1)
(kill k5)
(ctl k1 :smin 11 :smax 19)
(ctl h1 :smin 4)

(ctl k1 :div 1)
(ctl k2 :div 1)
(ctl k3 :div 3)
(ctl h1 :div 3)
(chord 4 :Cmajor)
(kill kick)

(ctl kick :freq (midi->hz (note (choose [:c2 :d#2 :d3 :g2 :g5 :d3 :d#4]))))
(ctl kick :freq 30 :facc 1000 :dur 0.61 :ft 0.002 :feed 0)
(ctl kick :div 2)
(fx-chorus 0 0.2 0.8)
(fx-chorus 1 2 0.2)
(fx-reverb 0)
(fx-echo 0 2 0.3 3)

(ctl 91 :delay-time 0.03 :decay-time 0.51)



(ctl kick :smin 0 :smin 32)
(source fx-echo)

(def e1 (fx-echo-my 0 2 0.034 0.4))
(def e2 (fx-echo-my 0 2 0.054 0.2))
(def e3 (fx-echo-my 0 2 0.134 0.14))
(def e5 (fx-echo-my 0 5 0.24 0.94))
(kill e5)
(kill 93)
(defsynth fx-echo-my
  [bus 0 max-delay 1.0 delay-time 0.4 decay-time 2.0]
  (let [source (in bus)
        pank   (lf-noise2:ar 0.3)
        echo   (comb-n source max-delay delay-time decay-time)
        echol  (* (neg pank) echo)
        echor  (* pank  echo)]
    (replace-out:ar bus [(+ echol source) (+ echor source)] )))

(kill 90)
(kill 52)
(node-tree)
(demo 30 (out 0 (pan2 (* (sin-osc 80) (decay (in:kr cbus) 0.2)))))


(source kick)
(kill kick)
(kill fx-echo-my)
(node-tree)
