(stop)
(demo 100
  (let [t  (impulse:kr 4)
        fe (env-gen (perc 0 0.01 1300 -50) t 1 40 1 )
        ae (env-gen (perc 0 0.3 1 -5) t 1 0 1)
        s  (sin-osc fe)
        s (distort (* (+ (* 4 ae) 10) (mix [s (* 0.2 (white-noise))])))
        sa (* s ae)
        sad (resonz sa fe 0.9)
        sad (mix [sa sad])
        sad (free-verb sad
                       (* 0.5 (- 1 ae))
                       0.39
                       0.19)
        sad (normalizer sad)

        x1 (demand t 0 (dseq:dr [4 2 4 2 8 4 2 4 2] INF))
        t2 (impulse (* 2 x1))

        g  (env-gen (perc 0 (/ 0.5 x1) 1 5) t2 1 0 1)
        sad (* sad g)
        sad2 (* sad g)
        x2 (demand t 0 (dseq [4 8 2 4 8 4 2 4 8] INF))
        t3 (impulse (* 2  x2))
        g2  (env-gen (perc 0 (/ 0.5 x2) 1000 -50) t3 1 0 1)
        saf (freq-shift sad g2 (sin-osc 2))

        sad (select (demand t3 0 (drand [0 1] INF)) [sad saf])
        t4 (impulse 1/4)
        x3 (demand t3 0 (drand [1/64 1/24 1/48 1/32 1/28 1/128] INF))
        m  (demand t4 0 (dseq [60 62 63] INF))
        m2 (demand t3 0 (dseq [0 24 -24] INF))
        x4 (demand t3 0 (drand [5 4 2 6] INF))
        cm (free-verb (freq-shift (comb-n sad 1 x3 0.129)
                                  (+ (midicps (- (+ m2 m) 24) ) (* g2 x4)) (sin-osc 1))
                      (- 1 g2)
                      0.95
                      0.1)

        sad (select (demand t3 0 (dseq [0 1 2 3 2 0 1 3] INF)) [sa sad2 sad cm])
        sad (mix [sad cm saf sad2])
        sad (distort (* (* sad2 10 saf) sad))
                                        ;  sad (normalizer sad)

        ]

    (out 0 (pan2 sad 0 1))))
(stop)
(k)

(def k1 (k))
(stop)
(odoc dwhite:dr)
(stop)
(demo 1 (out 0 (pan2 (sin-osc 200))))
