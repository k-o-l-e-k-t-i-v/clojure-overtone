(demo 80
      (let [m (env-gen (perc 10 70 1 10) 1 1 0 1)
            s (formant (+ (* 800 (env-gen (perc 0 0.001 1 0) (impulse (choose [ 100 30 60 120])) 1 0 1)) (* (choose [0.5 2 1 1.25 3 7 6 9]) (choose [440 (* 2 440) (/ 440 2) (/ 440 3)]))) (lin-lin  (lf-noise2 (choose [2 7 10 20 40])) -1 1 100 2000)
                       (lin-lin  (lf-noise2 (choose [2 7 10 20 40])) -1 1 100 2000)
                       )
            t (a2k  (dust 20))
            t2 (a2k (dust 14))
            g (env-gen (perc 0.9 0.92 1 0) t 1 0 1)
            g2 (env-gen (perc 0.8 0.82 1 0) t2 1 0 1)
            s (* s g)
            s (g-verb s 200 19 0.245)
            sm (* s m)]
        (out 0 (pan2 sm 0 0.05))))
(stop)
