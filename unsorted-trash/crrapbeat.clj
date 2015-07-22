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

(definst kicker
  []
  (let [l (local-in:kr 1)
        d (demand l (impulse (demand l 0 (drand [1/2 3/4 1/3 1]))) (dseq [4 8 4 8 4 2 8 8 8 8 4 2] INF))
        p (demand l 0 (dseq [40 80 40 80 40 30 16 32 48] INF))
        d2 (demand l (impulse 1/18) (dseq [1/32 1/64 1/48 1/12 1/24] INF))
        d3 (demand l 0 (dseq [1/60 1/30 1/30 1/20 1/24 1/50] INF))
        selector (demand l 0 (drand [0 1 2 4] INF))
        t (impulse:kr d)
        ae (env-gen (perc 0 0.3 1 -10)
                    t 1 0 1)
        fe (env-gen (perc 0 0.1 2000 -10) t 1 (midicps p) 1)
        fm (* (lin-lin p 16 80 100 2000)  (lf-noise2 6000))
        ffe (env-gen (perc 0 0.1 2 -10) t 1 0.2 1)
        s (sin-osc-fb (+ fm fe) ffe)
        s1 (comb-c s 1 d2 0.9)
        s2 (comb-c s 1 d3 0.9)
        sr (free-verb s 0.96 0.98  0.3)
        sl (select:ar selector [s s1 s2 sr])
        sl (freq-shift sl (* 80 (lf-noise2 4)))

        sa (* sl ae)
        so (free-verb sa 0.199 0.399 0.19)
        so (normalizer so 1 0.01)
        so (mix [so sa])
        ]
    (out 0 (pan2 so))
    (local-out:kr t)))

(def m (metronome 60))
(stop m)
(stop)
(at (m (m))
    (kill k1)
    (def k1 (kicker)))
