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

(stop)
(demo 60
      (let [tt (local-in:kr 1)
            ma (env-gen (envelope [0 1 1 0] [10 40 10]) 1 1 0 1)
            d (demand tt 0 (dseq [ 1 4 1 4 1 6 1 3] INF))
            t (impulse d)
            fe (env-gen (perc 0 0.8 (* 1000 d) -40) t 1 80 1)
            c (sin-osc fe)
            a (env-gen (perc 0 0.2 1 10) t 1 0 1)
            ca (* c a)
            r (free-verb c
                         0.9
                         0.6
                         0.5)
            d2  (demand tt 0 (drand [8 12 8 4 16 6] INF))
            t2 (impulse d2)
            a2 (env-gen (perc 0 (/ 1 d) 1 -10) t2 1 0 1)
            ra (* r a2 ma)
            ra (comb-n ra 2 (+ 0.2 (* 0.18 (lf-noise2 d2))) 0.9)
            ra (distort (* 30 ra))
            ra (freq-shift ra (* 4 d))
      ;      ra (normalizer ra)
            ]
        (out 0 (pan2 ra (sin-osc 1/3) 1))
        (local-out:kr (select:kr (lin-lin:kr  (lf-noise2:kr 0.2) -1 1 0 2.1) [t t2 tt]))))
(stop)
