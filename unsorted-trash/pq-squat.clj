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


(demo 30
      (let [e (env-gen (envelope [0 1 1 0] [5 20 5]) 1 1 0 1)
            tk (impulse:kr 3)
            t2 (demand tk 0 (dseq [8 16 32 8 1 8 16] INF))
            m (demand tk 0 (drand [ 80 100 400 30] INF))
            t (impulse t2)
            d (decay t 0.1)
            s (sin-osc-fb m (lin-lin:ar  (lf-noise2:ar 2) -1 1 0.2 1))
            sd (* s d e)
            r (free-verb sd
                         0.9 0.9 0.3)]
        (out 0 (pan2 r))))


(demo 100
      (let [t (impulse 4)
            s (sin-osc 30)
            d (env-gen (perc 0 0.42 1 -5) t 1 0 1)
            sd (* s d)
            dst (distort (* sd (* 4 d)))]
        (out 0 (pan2 dst (sin-osc 2) 1))))

(stop)

(dotimes [n 4 ]
  (demo 100
        (let [e (env-gen (envelope [0 1 1 0] [10 80 10]) 1 1 0 1)
              s (sin-osc (+ 3000 (* 1200
                                    (lf-noise2 5)
                                    (lf-noise2 5)
                                    (lf-noise2 5)
                                    (lf-noise2 5))))
              a (decay (impulse 1/4) 0.2)

              sa (* s a)
              r (free-verb sa
                           0.9 0.9 0.3)
              n (* e (normalizer r))
              d (demand (impulse 1) 0 (drand [4 6 8 2] INF))
              n (* n (decay (impulse d) 0.2))
              n (comb-c n 2 0.125 1)
              n (* n (decay (impulse 2) 0.3))
              ]
          (out 0 (pan2 n (sin-osc (rand 6)) 1)))
        ))
(stop)
