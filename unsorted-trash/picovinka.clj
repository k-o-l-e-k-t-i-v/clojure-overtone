;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone
(definst metro-synth
  [rate   30
   outbus 0
   phs    0]
  (let [t       (impulse:kr rate phs)
        counter (stepper:kr t :min 1 :max 32)]
    (send-trig:kr t counter)
    (out:kr outbus t)))


(def cbus1 (control-bus))

(do
  (def ms1 (metro-synth [:head] :outbus cbus1 :rate 3))
  (def ms2 (metro-synth [:head] :outbus cbus1 :rate 8)))

(kill metro-synth)
(ctl ms2 :freq 8)
(ctl ms3 :phs 0.75)
(ctl ms4 :phs 0.25)
(ctl ms1 :phs 0)
(ctl metro-synth :rate 1)

(definst boom
  [div    4
   amp    0.3
   freq   30
   ft     0.1
   facc   400
   r      0.3
   feed   0.17
   a      0.03
   outbus 0
   inbus  10
   sv     1]
  (let [tr (pulse-divider:kr (in:kr inbus) div sv)
        fenv (+ freq (* facc (decay tr ft)))
        aenv (decay tr r)
        src (sin-osc-fb fenv feed)
        sig (* aenv src)
        sig (free-verb sig 0.8 0.497 0.71)
        sig (distort sig)
        sig (normalizer sig 0.5 0.153)
        paner (* 0.3 (lf-noise2:ar 0.4))
        ]
    (out outbus (pan2 sig paner 1))
    ))



(def b1 (boom [:tail] :inbus cbus1 :facc 20 :freq 80 :div 3))
(def b2 (boom :inbus cbus1 :facc 200 :freq 4000 :div 6 :r 0.1 :feed 4))
(def b3 (boom :inbus cbus1 :facc 2000 :freq 40 :div 7 :feed 6))
(def b8 (boom :inbus cbus1 :facc 140 :freq 420 :div 5.5 :feed 2 :ft 0.3 :r 0.8 :sv 2.75))
(ctl b3 :div 13)
(ctl b1 :facc 1000 :ft 0.072 :r 0.56 :freq 50)
(ctl b1 :div 3)
(ctl b1 :div 2)
(kill b1)
(kill b3)
(kill boom)
(ctl boom :freq 90 :facc 8000 :ft 0.02 :feed 0.1)
(stop)
