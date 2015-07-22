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


(def bus-0 (control-bus 1))

(defsynth master-ticker
  [rate 1/4
   outb 0]
  (let [t (impulse:kr rate)]
    (out outb t)
    (send-trig:kr t 1 1)))

(node-tree)
(stop)
(oneshot-event "/tr" (fn [v] (println v)

                       (def k1 (kokoro))) ::debug)

(kill k1)
(kill 82)
(def mt-0 (master-ticker))
(kill kokoro)
(inst-pan! k1 0.3)
(inst-volume! k1 0.2)
(odoc inst-volume!)
(definst kokoro
  []
  (let [t  (local-in:kr 1)

        d1 (demand t (impulse 1/8) (drand [1 3 4 8 ] INF))
        r  (impulse:kr (/ 1 d1))
        d2 (demand t r (dseq [4 4 4 4 8 8 4 2] INF))
        d3 (demand t r (dseq [4 4 2 4 4 8] INF))
        to (impulse d3)
        to2 (impulse d2)
        fe (env-gen (perc 0 0.1 30 -10) to 1 0 1)
        fm (lag (midicps (+ (demand to 0 (dseq [-12 12 24 -14 7] INF)) (demand to 0 (dseq [50 52 53 59 57 59 45 52] INF))))
               0.1)
        s  (freq-shift (square (+ fm fe))
                       (* 100 fe)
                       (* 10 fe))
        ae (env-gen (perc 0 0.5 0.5 -3) to 1 0 1)
        sa (* s ae)
        sa (free-verb sa
                      0.5
                      0.9
                      0.2)
        sa (resonz (distort (* (+ 1 (* 2 fe)) sa)) (+ 1000 (* 10 fe)) 0.8)
        sa (normalizer sa)
        am (* -1 (env-gen (perc 0 0.31 0.2 0) to2 1 -1 1))
        sam (* sa am)
        ]
    (out 0 (pan2 sam (lf-noise2 1)))
    (local-out:kr to)))


(kill mt-0)
(stop)

(find-doc "handler")
(odoc send-trig)
