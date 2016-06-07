;;
;;     MM""""""""`M
;;     MM  mmmmmmmM
;;     M`      MMMM 88d8b.d8b. .d8888b. .d8888b. .d8888b.
;;     MM  MMMMMMMM 88''88'`88 88'  `88 88'  `"" Y8ooooo.
;;     MM  MMMMMMMM 88  88  88 88.  .88 88.  ...       88
;;     MM        .M dP  dP  dP `88888P8 '88888P' '88888P'
;;     MMMMMMMMMMMM
;;
;;         M""MMMMMMMM M""M M""MMMMM""M MM""""""""`M
;;         M  MMMMMMMM M  M M  MMMMM  M MM  mmmmmmmM
;;         M  MMMMMMMM M  M M  MMMMP  M M`      MMMM
;;         M  MMMMMMMM M  M M  MMMM' .M MM  MMMMMMMM
;;         M  MMMMMMMM M  M M  MMP' .MM MM  MMMMMMMM
;;         M         M M  M M     .dMMM MM        .M
;;         MMMMMMMMMMM MMMM MMMMMMMMMMM MMMMMMMMMMMM  Version 1.0beta24
;;
;;           http://github.com/overtone/emacs-live
;;
;; Hello Jr, it's lovely to see you again. I do hope that you're well.


(demo 100
      (let [s (sin-osc (+ 40 (* 18000 (lf-tri 1))))]
        (out 0 (pan2 s))))
(stop)

(demo 100
      (let [s (white-noise)
            s2 (sin-osc-fb 60 2)
            o (distort (* (* 20 (lf-noise2 2)) (white-noise) (mix [s s2])))]
        (out 0 (pan2 o))))
(stop)
(demo 10
      (let [td (demand (impulse 9) 0 (drand [1 2 1/4 1/3 9] INF))
            m (demand (impulse td) 0 (drand [40 60 56 57 23 45 47] INF))
            s   (sin-osc (midicps (* 2.76  (+ (* 10 (lf-noise2 10)) m))))
            s2 (sin-osc (* 3.2  (midicps m)))
            s3 (freq-shift  (sin-osc (* 1.62  (midicps (+ 10 (lf-noise2 8) m)))) 3000)
            s (mix [s s2 s3])
            s (distort (* (* (lf-noise2 8) 20) s))
            f (lin-exp (lf-tri (+ 30 (* 29 (lf-noise2 2)))) 0 1 200 6000)
            s (rlpf s f 1)
            s (distort (* 10 s))
            s (g-verb s 2 1 0.5)
            s (normalizer s)
            s (distort (* 20 s))
            ds (demand (impulse 1) 0 (dseq [ 1 4 1 8 6 1 3 5 1 9] INF))
            s (wrap:ar s (lf-noise2 ds) (lf-noise2 ds))
            s (normalizer s)
            s (distort (* 200 s))
            p (pulse (+ 2.01 (* 2 (lf-noise2 3))) (lin-lin (lf-noise2 2) -1 1 0 1))
            s (* p s)
            ]
        (out 0 (pan2 s))))
(odoc wrap)
(stop)
(demo 1000
      (let [s (sin-osc-fb (mouse-x:kr 40 1000 0 0.6) 2)
            s (distort (* 10 s))
            fx (mouse-y 0 1 0 0.6)
            fx2 (mouse-x 1 0 0 0.6)
            g (g-verb (* fx s) 100 10 0.3)
            s2 (square (+ 30 (* 200 fx2)))
            o (mix [(* fx  s) (* fx  s2) g])
            o (normalizer o)]
        (out 0 (pan2 o))))
(stop)



(demo 10
      (let [ m (env-gen (perc 2 8 1 -10) 1 1 0 1)
            d (demand (impulse:ar (+ 3.3 (* 3 (lf-noise2 8)))) 0 (drand [40 50 67 89 34 56 74] INF))
            s (apply + (into []  (repeatedly 20 (fn []  (formant:ar (+ (midicps d) (rand 9) (* 30 (lf-noise2 (rand 20))))
                                                                   (+ 30 (* 2.9 (lf-noise2 (rand 2))))
                                                                   (+ 30 (* 2.9 (lf-noise2 (rand 2)))))))))
            ;s (g-verb s 100 20 0.7 :drylevel 0.4)
            sm (* s m)]
        (out 0 (pan2 sm))))
(stop)





;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                        ;;
;; THE REAL FUCKING STUFF THAT WAS PLAYED ;;
;;                                        ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(stop)
(demo 120
      (let [m (env-gen (perc 20 100) 1 1 0 1)
            dm (demand (impulse 40) 0 (drand [ 40 47 40 49 40 50 43] INF))
            dm (midicps dm)
            s  (square (+ (* 20 (lf-noise2 1200)) (* (rand 5) dm)))
            at (impulse 1/4)
            e (env-gen (perc 0 2 1 0) at 1 0 1)
            s (rlpf s (lin-exp (lf-noise2 1000) -1 1 300 5000) 2)
            se (* s e)
            sem (* s e m)
            ]
        (out 0 (pan2 sem 0 1))))


(demo 30
      (let [m (env-gen (perc 2 28 1 0) 1 1 0 1)
            e (env-gen (perc 0 0.1 1 0) (dust:kr 9) 1 0 1)
            dm (demand (impulse 4) 0 (dseq [40 41 42 43 44 56 47 43] INF))
            s (square (midicps dm))
            s (resonz s (+ 620 (lin-lin (lf-noise2 10) -1 1 0 4000)) 2)
            s (distort (* 2 s))
            ;s (g-verb s 100 10 0)
            ;s (normalizer s)
            s (* s e)
            s (comb-n s 2 (+ 0.2 (* 0.19 (lf-noise2 40))) 1)
            sm (* s m)
            ]
        (out 0 (pan2 sm 0 1))))

(demo 100
      (let [x (env-gen (perc 20 80 1 -10) 1 1 0 1)
            s (sin-osc-fb (midicps (note (choose [:C2 :D2 :G2 :A2 :D2 :B2 :C7]))) 2)
            s (freq-shift s (* 2000 (lf-noise2 200)))
            s (g-verb s 200 10 0.3)
            s (wrap:ar s (lf-noise2 3) (lf-noise2 2))
            s (normalizer s)
            sx (* x s)]
        (out 0 (pan2 sx (lf-noise2 2) 1))))

(demo 10
      (let [td (demand (impulse 9) 0 (drand [1 2 1/4 1/3 9] INF))
            m (demand (impulse td) 0 (drand [40 60 56 57 23 45 47] INF))
            s   (sin-osc (midicps (* 2.76  (+ (* 10 (lf-noise2 10)) m))))
            s2 (sin-osc (* 3.2  (midicps m)))
            s3 (freq-shift  (sin-osc (* 1.62  (midicps (+ 10 (lf-noise2 8) m)))) 3000)
            s (mix [s s2 s3])
            s (distort (* (* (lf-noise2 8) 20) s))
            f (lin-exp (lf-tri (+ 30 (* 29 (lf-noise2 2)))) 0 1 200 6000)
            s (rlpf s f 1)
            s (distort (* 10 s))
            s (g-verb s 2 1 0.5)
            s (normalizer s)
            s (distort (* 20 s))
            ds (demand (impulse 1) 0 (dseq [ 1 4 1 8 6 1 3 5 1 9] INF))
            s (wrap:ar s (lf-noise2 ds) (lf-noise2 ds))
            s (normalizer s)
            s (distort (* 200 s))
            p (pulse 2 (lin-lin (lf-noise2 2) -1 1 0 1))
            s (* p s)
            ma (env-gen (perc 2 8 1 -10) 1 1 0 1)
            sma (* s ma)
            ]
        (out 0 (pan2 sma))))

(demo 40
      (let [ma (env-gen (perc 10 30 1 0) 1 1 0 1)
            dm (demand (impulse 2) 0 (drand [30 90 32 96 78 45 36 24] INF))
            s (square (* 2 (midicps dm)))
            df (demand (impulse 6) 0 (drand [40 60 70 30 40 90] INF))
            s (rlpf s (* (+ 3 (* 0.2 (lf-noise2 0.3))) (midicps df)) 0.9)
            s (freq-shift s (* 2 (lf-noise2 1)))
            s (g-verb s 90 7 0.2 :drylevel 0.6)
            sma (* s ma)
            ]

        (out 0 (pan2 sma 0 0.4))))

(demo 10 )
