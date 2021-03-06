(do (use 'overtone.core)
    (use 'late-night.core))


(println (chord :C5 :maj))
(odoc pitch)
(stop)
(demo 100 (let [e (env-gen (perc 10 30 1 10)
                           1 1 0 1)
                s (sin-osc (round (lin-lin (lf-noise2 (lin-lin (lf-noise2 7)
                                                               -1 1 1 8))
                                           -1 1 30 200)
                                  (lin-lin (lf-noise0 (lin-lin (lf-noise2 7)
                                                               -1 1 1 8))
                                           -1 1 30 200)))
                s (wrap:ar (* 12.3 s) -0.1 1)
                s (rlpf s (+ 10 (round (lin-lin (lf-noise1 2.7) -1 1 300 4000)
                                          (lin-lin (lf-noise0 3.18) -1 1 300 8000)))
                        0.97)
                s (normalizer s)
                s (free-verb s 0.29 0.49 0.3)
                e2 (env-gen (perc 0.7 0.1 1 10)
                            (dust:kr 3) 1 0 1)
                s (* e s e2)
                s (wrap:ar (* 1.3 s) -1 0.1)
                s (normalizer s)
                s (g-verb s 100 4 :taillevel 0.5 :drylevel 0.9)
                s (wrap:ar (* 1.3 s) -1 1)
                s (rlpf s (lpf (+ 40 (round (lin-lin (lf-noise1 2.7) -1 1 300 4000)
                                            (lin-lin (lf-noise0 3.18) -1 1 300 8000)))
                               20)
                        0.97)
                s2 (comb-n s 8 (lpf (lin-lin (lf-noise0 3) -1 1 0.0001 0.93)
                                    100) 8)
                s (mix [s s2])
                s (normalizer s)
                ;s (g-verb s 30 6.63 :taillevel 0.7272)
                ;s (normalizer s)
                ]
            (out 0 (pan2 s (* 0.13 (lf-noise2 3)) 0.3))))
(stop)
