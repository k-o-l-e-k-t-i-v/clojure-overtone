(do (use 'overtone.core)
    (use 'late-night.core))

(def cb (control-bus 1))
(def gr-1 (group))
(def gr-2 (group :after gr-1))

(defsynth tsynth
  [outbus 0]
  (out outbus (impulse 1)))
(defsynth ssynth
  [inbus 0]
  (out 0 (* (saw 100) (env-gen (perc 0 0.3 1 0) (in:kr inbus) 1 0 1))))

(def tt (tsynth [:head gr-1] :outbus cb))
(def ss (ssynth [:head gr-2] :inbus cb))

(def ss2 (ssynth :inbus cbus))
(kill ss)
(stop)
(defsynth ssynth2
  [inbus 0]
  (out 0 (* (saw 100) (env-gen (perc 0 0.3 1 0) (impulse 1) 1 0 1))))

(node-tree)
