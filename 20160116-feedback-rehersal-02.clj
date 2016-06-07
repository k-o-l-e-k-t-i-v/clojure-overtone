(use 'overtone.core)
(use 'late-night.core)
(demo 1
      (out 0 (pan2 (sin-osc 400) 0 1)))

(stop)
(demo 1000000
      (out 0 (pan2 (in:ar 8) 0 1)))


(demo 10000
      (out 0 (pan2 (normalizer (freq-shift (in:ar 8) 40)) 0 1)))

(defsynth pitch-shift-g
  [f 400]
  (let [i (in:ar 8)
        inn (normalizer i)
        x (bpf i f 0.151)
        x (freq-shift x f)
        x (normalizer x)
        e (env-gen (perc 0.5 0.5 1 0)
                   1 1 0 1)
        x (* e x)
        x (comb-n x 1 0.3 0.93)
        x (g-verb x 130 7)
        sil (detect-silence x 0.01 0.02 FREE)
        ]
    (out 0 (pan2 x))))

(pitch-shift-g :f 8)
(stop)
(def m-1 (metronome 480))

(metro-bpm m-1 (* 120 1))

(def p-1 (ref [1 0 0 0
               0 0 0 0
               0 0 0 0
               0 0 0 0]))

(defn s []
  (pitch-shift-g  :f (+ (first @pm-1) (first @pm-2)))
  (dosync (ref-set pm-1 (rotate 1 @pm-1)))
  (dosync (ref-set pm-2 (rotate 2 @pm-2))))

(s)
(at-zero-beat m-1 1 p-1 #'s)
(dosync (ref-set p-1 [1 0 1 0
                      1 0 1 0]))

(def pm-1 (ref [900 400 300 240 9000 4000 2300]))
(def pm-2 (ref [300 900 230 670 340]))


(dosync (ref-set pm-1 [900 30 600 30 400]))
(dosync (ref-set pm-2 [30 80 100]))
