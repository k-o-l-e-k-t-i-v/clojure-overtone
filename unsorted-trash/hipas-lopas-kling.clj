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
;; Jr, turn your head towards the sun and the shadows will fall behind you.


()

(stop)
(def demand-bus (control-bus 1))
(definst kolt
  [k 4
   mn 90]
      (let [tx (demand:kr (local-in:kr)
                          0
                          (dshuf [2 4 4 4 6 3 3 3 8 8]
                                INF))
            t (impulse k)
            m (midicps (+ mn
                                   (demand t
                                           0
                                           [(dshuf [0 12 0 11 3 2 0 14 15 9 8]
                                                  INF)])))
            s (apply mix (list ( sin-osc [m (+ 24.1 m) (+ 48.03 m)])))
            a (decay t 0.3)
            sa (* s a)
            sar (free-verb sa
                           (lin-exp (lf-noise2 1) -1 1 0.2 0.9)
                           (lin-exp (lf-noise2 1) -1 1 0.2 0.9)
                           (lin-exp (lf-noise2 1) -1 1 0.2 0.9)
                           )
            sarm (mix [(resonz sar (lin-exp  (decay t 0.21) 0 1 40 3000) 0.9)
                       (rhpf (* 0.6 sa)
                             (lin-exp (sin-osc  (* (decay t 0.72) 300 (lf-noise2 4))) -1 1 300 9000)
                             (lin-exp (sin-osc 100) -1 1 0.3 0.9))])]
        (out 0 (pan2 sarm))
        (local-out:kr t)))
(stop)






(def m1 (metronome 120))
(m1)
(m1 48)



(m1 :bpm 60)

(at (m1 (m1))
    (kolt :k 3/5 :mn 90))
(stop)
