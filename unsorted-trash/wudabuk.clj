(do (use 'late-night.core)
    (use 'overtone.core)
    (use 'late-night.utils))


(stop)
(def g1 (group))
(def g2 (group :after g1))
(node-tree)

(def sig-bus (audio-bus 2))


(definst
  glnk
  [fb 230
   fl [20 30 8 7]])

;(odoc klang)


(definst bd1 [freq 100 amp 1 wm 18 dist 1]
  (let [s (klang:ar [[91  16   230 40  56  80]
                     [0.2 0.4  0.1  2.7 0.2 0.8]
                     [0.1 0.3  0.9  0.3 0.1 0]]
                    0.3
                    freq)
        s (normalizer s)
        s (wrap:ar (* wm s) -1 1)
        flte (env-gen (perc 0 0.03 1 0)
                      1 3000 80 1)
        s (rlpf s (+ (* 10 (lf-noise2 10)) flte) 0.95)
                                        ;               s (normalizer s)

        fe (env-gen (perc 0 0.05 1 0)
                    1 -1000 0 1)
        s (freq-shift s (+ (* 10 (lf-noise2 2)) fe))
        s (distort (* dist s))
        e (env-gen (perc 0.1 3 1 -20)
                   1 1 0 1 FREE)
        s (* s e)]
    (out sig-bus (* s amp))))
(bd1)
(stop)
(node-tree)

(def p1 (ref [1 1 0 1 1 0 0 0 1 1 0 1 1 0 0 0]))
(def m1 (metronome (* 3 120)))
(defn bd1-p1 []
  (bd1 [:head g1] :freq 0 :amp 0.5 :wm 17 :dist 4))

(def p2 (ref [1 0 1 0 0 1 0 1 0 0 1 0 0 1 0 1]))
(at-zero-beat m1 8 p1 #'bd1-p1)

(defn bd1-p2 []
  (bd1 [:head g1] :freq 8000 :amp 1 :wm 9 :dist 9))
(at-zero-beat m1 8 p2 #'bd1-p2)

(def p3 (ref [0 0 1 0 0 1 0 0 0 1 0 1 0]))
(defn bd1-p3 []
  (bd2 [:head g1] :freq 330 :amp 0.94 :wm 2 :dist 3))
(at-zero-beat m1 8 p3 #'bd1-p3)
(dosync (ref-set p3 [1 0 1 0 0 1 1 0 1 0 0 1 1 0 1 1 0 0 0]))
(stop)

(metro-bpm m1 (* 6 120))

(into [] (interleave (repeat 3 [1 0 0 1 0]) (repeat 3 [1 0 1 0 0 0])))
(definst bd2 [freq 100 amp 1 wm 18 dist 1]
  (let [s (lf-noise0 freq)
        s (normalizer s)
        s (wrap:ar (* wm s) -1 1)
        flte (env-gen (perc 0 0.8 1 -10)
                      1 -300 8000 1)
        s (bpf s (+ (* 700 (lf-noise2 100)) flte) 0.15)
                                        ;               s (normalizer s)

        fe (env-gen (perc 0 0.05 1 0)
                    1 -1000 0 1)
        s (freq-shift s (+ (* 10 (lf-noise2 2)) fe))
        s (distort (* dist s))
        e (env-gen (perc 0.01 0.3 1 -20)
                   1 1 0 1 FREE)
        s (* s e)]
    (out sig-bus (* s amp))))

(definst master [del 0 amp 1 lofr 1]
  (let [i (in:ar sig-bus)
        d1 (delay-n i 2 (lpf del lofr))
        a (* d1 (lpf amp lofr))]
    (out 0 (pan2 a 0 1))))
(def master-1 (master [:head g2] :del 0 :amp 1))
(ctl master-1 :amp 1 :del 0.3 :lofr 0.75)
(kill master-1)
