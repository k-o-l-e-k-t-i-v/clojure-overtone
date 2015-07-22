(defsynth
  k
  [a 1
   r1 2
   r2 6]
  (let [t (impulse (* a r1))
        t2 (impulse (* a r2))
        fe (env-gen (perc 0 0.1 1 -10) t 1200 40 1)
        fe2 (env-gen (perc 0 0.1 1 -10) t 2000 100 1)
        de (env-gen (perc 0 0.3 1 -10) t 20 1 1)
        ae (env-gen (perc 0 0.3 1 -10) t2 1 0 1)
        s (square fe)
        s (distort (* s de))
        s (g-verb s 90 20 0.1)
        sf (rlpf s fe2 2)
        o (* sf ae)
        ]
    (out 0 (pan2 o 0 0.96))))
(def k1 (k :r1 80))
(def k2 (k :r1 4))
(def k3 (k :r1 6))
(ctl k1 :r1 (choose [90 20 30 40 230 900]) :r2 (choose [90 200 3000 230 345 12]))
(ctl k3 :a 0)
(kill k1)

(stop)

(demo 30
      (let [e (env-gen (perc 0.6 0.92 1 0) (impulse 1/8) 1 0 1)
            s (mix [(lf-noise2 80)
                    (sin-osc (rand 8000))])
            s (freq-shift s -70)
            se (* s e)
            g (distort (* (* 10 e) se))
            g (freq-shift g (* 100 (lf-noise2 300)))
            g (g-verb g 200 40 0.92)
            g (normalizer g)
            g (freq-shift g (rand 1000))
            ]
        (out 0 (pan2 g 0 0.21))))

(demo 100
      (let [i (lf-noise1 1800)
            i (g-verb i 80 10 0.2 1 1 0.3)
            x (normalizer i)
            x (distort (* 2 x))
            x (freq-shift x 100)
            x (g-verb x 90 20 0.9)
            x (* x (env-gen (perc 0.41 0.42 1 0) (a2k (dust 6)) 1 0 1))
            ]
        (out 0 (pan2 x 0 1))))
(stop)
