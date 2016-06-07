(do (use 'overtone.core)
    (use 'late-night.core))

(dotimes [n 1]
  (demo 30 (let [s (distort (* 200 (saw (lin-exp (lf-tri (lin-lin (lf-noise2 90)
                                                                 -1 1 2 170)) 0 1 300 4900))))
                 e (env-gen (envelope [0 1 1 0]
                                      [4 22 4])
                            1 1 0 1 FREE)
                 s (freq-shift s 30)
                 s (g-verb s 30 5)
                 d (demand (impulse 2) 0 (drand [8 2 4 6]))
                 e2 (decay (impulse d) 0.1)
                 s (normalizer s)
                 s (* s e e2)
                 ]
             (out 0 (pan2 s)))))
(stop)
(dotimes [n 10]
  (demo 20 (let [s (saw (midicps (note (choose [50 66 67 69]))))
                 e (env-gen (perc)
                            (impulse (choose [2 4 6 1/3 1 1/4]))
                            1 0 1)

                 s (* s e)]
             (out 0 (pan2 s)))))


(definst b [fm 3000
            fb 80]
  (let [ef (env-gen (perc 0 0.002 1 0)
                    1 fm fb 1)
        ea (env-gen (perc 0 0.32 1 0)
                    1 1 0 1)
        s (sin-osc ef)
        s (distort (* s 200))
        s (* s ea)
        d (demand (impulse 30) 0 (drand [ 0.5 0.5 0.9]))
        s (comb-c s 1 d 1 )
        sil (detect-silence s 0.01 0.2 FREE)]
    (out 0 (pan2 s 0 1))))
(b)
(stop)
(defn bb []
  (b :fb 40 :fm 2900))

(def p1 (ref [1 0 0 1 0 0 1 0]))
(def m1 (metronome 240))
(at-zero-beat m1 8 p1 #'bb)
(dosync (ref-set p1 [1 1 1 0]))

(dotimes [n 10]
  (demo 15 (let [b (+ (demand (impulse 10) 0 (dser [40 52 64 76] INF)))
                 x (mix [ (saw (midicps b))
                          (square (midicps (+ (choose [7 12 19]) b )))])

                 x (bpf x (lin-lin (lf-noise2 3) -1 1 30 1000) 0.02)

                 e (env-gen (perc 5 10 1 0)
                            1 1 0 1 FREE)
                 x (g-verb x 90 4)
                 x (normalizer x)
                 x (* x e)
                 ]
             (out 0 (pan2 x 0.017)))))

(stop)
(dotimes [n 30]
  (demo 20 (let [a (choose [7000 400 500 6000 3254 256])
                 b (choose [235 467 985 40 30])
                 t (choose [ 1 3 5 8])
                 f (line a b 20)
                 s (sin-osc-fb (lin-lin (lf-noise2 4) -1 1 30 f) 2)
                 e (env-gen (perc 0 0.0126 1 0)
                            (impulse t) 1 0 1)
                 s (distort (* 1 s))
                 s (* s e)
                 s (wrap:ar (* 30 s) -1 1)]
             (out 0 (pan2 s 0 0.933)))))
