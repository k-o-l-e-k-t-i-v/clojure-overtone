(use 'overtone.core)
(use 'late-night.core)
(use 'late-night.synths)


                                        ;(demo 2 (out 0 (pan2 (white-noise:ar))))



(demo 10 (let [s (mix [(sin-osc-fb (lin-lin (lf-noise2 3) -1 1 400 9000) 0.3)])
               s (distort (* s 20))
               f (resonz s (lin-lin (lf-noise2 12) -1 1 400 12000) 0.9)
               f (distort (* 3 f))
               f (* (pulse:ar 2) f)
               f (comb-n f 2 (lin-lin (lf-noise2 3) -1 1 0.001 0.5) 1)
               f (free-verb f 0.99 0.9)
               e (env-gen (perc 4 6 1 0) 1 1 0 FREE)
               fe (*  e)
               ]
           (out 0 (pan2 fe (lf-noise2 3)))))
(stop)





(def p1 (ref [1 0 0 0 1 0 0 0]))
(def m1 (metronome 480))






(use 'late-night.synths)


(defn sp []
  (s)
  )



(definst s []
  (let [s (sin-osc-fb 500 1.7)
        e (env-gen (perc 1 0 1 10) 1 1 0)
        se (* s e)
        c (comb-n se 5 (lin-lin (lf-noise2 1/2) -1 1 0.01 0.739) 4.6)
        c (freq-shift c (lin-lin (lf-noise2 8) -1 1 0 (demand (impulse 3) 0 (drand [0 300 1000]))))

        c (clip:ar c -1 1)

        sil (detect-silence c 0.01 0.15 FREE)]
    (out 0 (pan2 c (lf-noise2 5)) )))



(s)


(stop)

(at-zero-beat m1 1 p1 #'sp)
(dosync (ref-set p1 [1 1 1 1]))




(definst b []
  (let [e (env-gen (perc 0 0.01 1 0) 1 1000 (lin-lin:kr (lf-noise2:kr 90) -1 1 80 200) 80)

        s (sin-osc-fb e 2)
        s (distort (* (+ 1 e) s))
        s (rlpf s e 0.9)
        a (env-gen (perc 0 0.13 1 0) 1 1 0 )
        sa (* s a)
        sa2 (free-verb sa 0.63 0.24 0.19)
        sa (mix [sa sa2])
        sil (detect-silence sa 0.02 0.2 FREE)
        ]
    (out 0 (pan2 sa))))

(b)

(defn bb []
  (b))

(at-zero-beat m1 8 pb1 #'bb)


(dosync (ref-set pb1 [1 1 0 0 1 1 0 1 1 0 1 1 0]))
(stop)



(dotimes [n (rand 10) ]
  (demo 10 (let [e (env-gen (perc 5 5 1 0) 1 1 0)
                 s (saw (+ (env-gen (perc 0 0.4 1 0) 1 (rand 400) 0 )
                           (midi->hz (+  (choose [75 64 58 34 36 23 60 72 37 48 60 37 24])
                                         (* 0.097341 (choose [60 72 37 48 60 37 24]))))))
                 se (* s e)]
             (out 0 (pan2 se (lf-noise2 3))))))



DEKUJEME
