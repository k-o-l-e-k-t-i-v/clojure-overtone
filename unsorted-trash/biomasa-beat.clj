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
      (let [s (mix (sin-osc [(lin-exp (lf-tri (* 10 (lf-noise2 2))) -1 1 120 2040)
                             (lin-exp (saw (* 10 (lf-noise2 2))) -1 1 200 2240)
                             (lin-exp (saw (* 10 (lf-noise2 2))) -1 1 1270 1970)
                             (lin-exp (lf-tri (* 10 (lf-noise2 2))) -1 1 2700 3040)]))
            f (resonz s
                      (lin-exp (sin-osc 1/3) -1 1 40 3000)
                      (lin-exp (sin-osc 1/6) -1 1 0.14 1))
            a (env-gen:ar (perc 0.5 0.7 1 0) (impulse:ar (+ 1/4 (* 2 (lf-noise2 4)))) 1 0 1)
            af (* a f)
            af (freq-shift af (* 200 (lf-noise2 10)) (sin-osc 200))
            afr (free-verb af
                           0.352 0.997 0.09)
            n (normalizer afr 0.2)
            t (impulse:kr (select:kr (pulse-count (impulse 2) (impulse 1/7)) [3 7 4 9 2 4]))
            d (env-gen (perc 0 0.321 1 -10) t 1 0 1)
            bt (* d afr)
            r (free-verb bt
                         0.14 0.38 0.1)
            n2 (normalizer r 0.4 0.1)
            tk (impulse 4)
            tk2 (impulse 6)
            k  (* (decay tk 0.3)
                  (distort (* 3 (sin-osc-fb (lin-exp (decay tk 0.1) 0 1 40 3000)
                                             (lin-exp (decay tk 0.04) 0 1 0 0.3)))))
            k2  (* (decay tk2 0.3)
                   (sin-osc-fb (+ (* (* 2000 (lf-noise2 3)) (lf-noise2 1000)) (lin-exp (decay tk2 0.01) 0 1 100 7000))
                              (lin-exp (decay tk2 0.1) 0 1 0 0.7)))
            s  (lpf (lin-lin (lf-noise2 4) -1 1 0 3) 50)
            s2 (lpf (lin-lin  (lf-clip-noise 4) -1 1 0 1) 20)
            nk (mix [(* s2 k) (* (- 1 s2) k2) n2 afr])
            nk2 (distort (* 3 nk))
            melt (impulse 1/7)
            melt2 (impulse 4/5)
            melt3 (impulse 1/15)
            melm (select (pulse-count melt2 melt3) [40 62 43 60 39 54 86 95 51 47 46])
            mels (* (lin-exp (decay melt2 1.3) 0 1 0.02 1)
                    (sin-osc (midicps (latch:ar melm melt2)) (* (decay melt 3) (sin-osc 4000))))
            mels (normalizer  (free-verb mels 0.93 0.9 0.3) 0.24)]
        (out 0 (pan2 (mix [nk2 mels]) (* 0.5 (lf-noise2 2))))))
(kill)
(stop)
(dotimes [n 10]
  (println (str n)))

(find-doc "select")
