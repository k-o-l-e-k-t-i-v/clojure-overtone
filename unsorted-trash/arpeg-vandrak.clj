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

(defsynth vandrak
  [])


(demo 20
      (let [m (demand (impulse 8) 0
                      (dseq (choose [[(note :C4)
                                      (note :D4)
                                      (note :e4)
                                      (note :g4)]
                                     [(note :C4)
                                      (note :d4)
                                      (note :g4)
                                      (note :A4)]
                                     [(note :C4)
                                      (note :a4)
                                      (note :F4)
                                      (note :b4)]])
                            INF))
            d (demand (impulse 1/4) 0
                      (dseq [1 0]))
            me (env-gen (adsr 1.5 0.5 0.5 6 1 -5 0)
                        d 1 0 1)
            s1  (sin-osc-fb (+ (rand 2)
                               (* 26 (lf-noise2 1300))
                               -2
                               (midicps  (+ 12 m)))
                           (lin-lin (lf-noise2 4300)
                                    -1 1 0 0.3))
            s2 (square (+ (rand 4) -2 (midicps  (- m 12))))
            s2 (rlpf s2 (+ (* 100 (lf-noise2 20) s1)
                           (env-gen (perc 0 0.62 1 0) d 1000 300 1)) 0.9)
            s2 (distort (* s2 8))
            g (dust:kr (* 1000 (lf-noise2:kr 12)))
            e (env-gen (perc 0.002 0.016 1 0) g 1 0 1)
            s3 (rlpf (white-noise) (* 1000 e) 0.9)
            s3 (freq-shift s3 (midicps (+ 24 m)))
            s3 (* e s3)
            s (mix [s1
                    ;s2
                    s3])
            s (g-verb s 300 3 0.93 0.1 0.1 0.62 0.2 1)
            sfx (freq-shift s (* 2.4 (lf-noise2 0.2)))
            sfx2 (freq-shift s (* 1.4 (lf-noise2 0.2)))
            s (free-verb  (limiter (mix [s sfx sfx2]) 1)
                       0.59 0.29 0.1)
            sme (* s me)
            ]
        (out 0 (pan2 sme 0 1))))
(stop)
