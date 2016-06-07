(do (use 'overtone.core)
    (use 'late-night.core))

(do
  (def synths (group))
  (def effx (group :after synths))
  (def master (group :after effx)))

(definst bok
  [amp 1
   di 1
   freq 40
   fmod 1000
   t 10
   s 400
   e 20
   outbus 0
   fsh 0]
  (let [me (env-gen (envelope [0 1 1 0]
                              [0 10 0])
                    1 1 0 1)
        l (lin-exp (pow (line:kr 0 1 t) 2) 0 1 s e)
        acc (impulse:kr l)
        enf (env-gen (perc 0 0.01 1 0)
                     acc fmod freq 1)
        ena (env-gen (perc 0 0.3 1 0)
                     acc 1 0 1)
        s   (sin-osc-fb enf di)
        s (* s ena me)
        v (freq-shift s (+ (* l 30) fsh))
        v (g-verb v :roomsize 100 :revtime 4 :taillevel 0.02 :drylevel 1 :earlyreflevel 0.0923)
                                        ;        n (normalizer v)
        s (wrap:ar s -0.0019 0.9)
        s (normalizer (resonz s (env-gen (perc 0 0.1 1 0)
                                         acc (* l 200) 40 1)
                              0.79))
                                        ;v (freq-shift v fsh)
        s (mix [ s
                v
                                        ;n
                ])
        x (detect-silence s 0.002 0.5 FREE )
        ]
    (out outbus (pan2 s (lf-noise2 4) amp))))

(defn expo []
  (do
    (bok [:head synths] :amp 0.7 :s 2 :e 16 :di 0.98 :fmod 4000 :fsh -800 :freq 87 :t 10)
    (bok [:head synths] :amp 0.47 :s 2 :e 16 :di 1.821 :fmod 2000 :fsh -300 :freq 84 :t 10)
    (bok [:head synths] :amp 0.27 :s 2 :e 16 :di 1.933 :fmod 6000 :fsh 420 :freq 210 :t 10)))

(expo)

(defsynth ttt
  [rate 10]
  (let [s (impulse:kr (/ 1 rate))
        ]
    (send-trig:kr s 1 0)))

(def ttt-synth (ttt))
(kill ttt-synth)

(on-trigger 1
            (fn [val] (println "trig val: " val))
            ::debug)

(on-trigger 1
            (fn [val] (expo) (println "expo was triggered"))
            ::expo)

(remove-event-handler ::pat-1)
