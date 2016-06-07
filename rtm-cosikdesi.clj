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

(defsynth boom
  [freq-mod 1800
   freq-base 40]
  (let [e (env-gen (perc 0 0.02 1 0) 1 freq-mod freq-base 1)
        e2 (env-gen (perc 0 0.02 1 0) 1 (* 0.5 freq-mod) freq-base 1)
        a (env-gen (perc 0 0.12 1 0) 1 1 0 1)
        a2 (env-gen (perc 0 0.12 1 0) 1 1 0 1)

        s (sin-osc-fb e 0.5)
        s (distort (* 4 e s))
        s (resonz s (* 4 e) 0.5)
        sa (* s a)

        s2 (saw e2)
        s2 (distort (* 40 e2 s2))
        s2 (rlpf s2 (* 1 e2) 0.915)
        sa2 (* s2 a2)

        saa (env-gen (perc 0 0.061 1 -5) 1 1 0 1)
        sa3 (g-verb:ar sa2 23 0.883 0.05)
        sa3 (freq-shift sa3 (* saa 2000))

        o (mix [sa sa2 (* 0.523 sa3)])
        o (distort (* 20 o) )

        x (detect-silence o 0.001 0.25 FREE)]
    (out 0 (pan2 o 0 1))))

(boom)
(def metro-1 (metronome (* 4 160)))
(def p1 (ref [1 0 0 0 1 0 0 0 1 0 0 0 1 0 1 1]))
(dosync (ref-set p1 [1 0 0
                     1 0 0
                     1 0 0 0 ]))
(seq-player metro-1 (metro-1) @p1 p1 boom)

(def p2 (ref [1 0 1 0]))
(dosync (ref-set p2 [1 0 1 0
                     1 0 1 0
                     1 0 1 0
                     ]))
(dosync (ref-set p1 [1 0 1 1 0 1 0]))
(dosync (ref-set p1 (rotate 1 @p1)))

(def g1 (group))
(def g2 (group :after g1))

(def f1 (ref [1000 20 40 500 300 100 500 200]))
(def f2 (ref [100 30 30 40 20 40 400 40]))


(dosync (ref-set f2 [50 20 10 20 10 20 30
                     ]))

(dosync (ref-set f1 [1000 100 100 100 100 100 10 20]))

(defn bd []
  (let [what (first @inst-list)]
    (what [:head g1] :freq-mod (first @f1)))
  (dosync (ref-set f1 (rotate 1 @f1)))
  ;(dosync (ref-set inst-list))
  )

(defn bd2 []
  (let [what (first @inst-list)]
    (what [:head g1] :freq-base (first @f2)))
  (dosync (ref-set f2 (rotate (rand-int 4) @f2)))
  (dosync (ref-set inst-list (rotate 1 @inst-list))))

(def inst-list (ref [boom hatboom boom hatboom]))
(dosync (ref-set inst-list [boom boom hatboom]))
(print @f1)

(stop)
(bd2)

(metro-bpm metro-1 (* 2 160))
(do
  (stop)
  (seq-player metro-1 (metro-1) @p2 p2 bd)
  (seq-player metro-1 (metro-1) @p1 p1 bd2))

(metro-tock metro-1)
(defsynth rep
  [g 0]
  (let [e (env-gen (asr 1 1 1 0) g 1 0 1)
        s (in:ar 0 1)
        s2 (in:ar 1 1)
        t (* 0.000125  (metro-tock metro-1))
        fx (comb-l s 2 t 1.5)
        fxd (demand (impulse t) 0 (dseq [0 10 0 0 100 0 0 200 0] INF))
        fx (freq-shift fx fxd)
        fx (normalizer fx)
        fx2 (comb-l s2 2 t 1.5)
        fxd2 (demand (impulse (* 3/4 t)) 0 (dseq [200 10 0 2000 100 0 0 200 0] INF))
        fx2 (freq-shift fx2 fxd2)
        fx2 (normalizer fx2)
        m (mix [(* (lin-lin e 0 1 2 0) s) (* 2 e fx)])
        m2 (mix [(* (lin-lin e 0 1 2 0) s2) (* 2 e fx2)])
        ]
    (replace-out:ar 0 m)
    (replace-out:ar 1 m2)))

(at (metro-1 (metro-1)) (def rep1 (rep [:head g2])))
(kill rep1)
(ctl rep1 :g 1)
(metro-bpm metro-1 (* 4 160))
(stop)

(hatboom)
(defsynth hatboom
  [freq-mod 90
   freq-base 4000]
  (let [e (env-gen (perc 0 0.02 1 0) 1 freq-mod freq-base 1)
        e2 (env-gen (perc 0 0.02 1 0) 1 (* 0.5 freq-mod) freq-base 1)
        a (env-gen (perc 0 0.2 1 0) 1 1 0 1)
        a2 (env-gen (perc 0 0.2 1 0) 1 1 0 1)

        s (sin-osc-fb e 4.5)
        s (distort (* 20 e s))
        s (rhpf s (* 4 e) 0.85)
        sa (* s a)

        s2 (sin-osc-fb e2 3)
        s2 (distort (* 20 e2 s2))
        s2 (bpf s2 (* 4 e2) 0.0155)
        sa2 (* s2 a2)

        saa (env-gen (perc 0 0.21 1 -5) 1 1 0 1)
        sa3 (g-verb:ar sa2 30 0.3 0.95)
        sa3 (freq-shift sa3 (* saa 6000))

        o (mix [sa sa2 (* 0.823 sa3)])
        o (wrap:ar o -0.05 0.35)
        oe (env-gen (perc 0 0.1 1 -5) 1 9000 1000 1)
        o (rlpf o oe 0.998)
        o (normalizer o)
        o (distort (* 100 o))
        me (env-gen (perc 0 0.14 1 -4) 1 1 0 1)
        o (* o me)

        x (detect-silence o 0.001 0.5 FREE)]
    (out 0 (pan2 o 0 1))))


(stop)

(def hpat-1 (ref [1 0 0 0
                  1 0 0 0
                  ]))
(dosync (ref-set hpat-1 [1 0 0 0]))
(stop)
(do
  (dosync (ref-set p1 [1 0 0 1 0 0]))
  (dosync (ref-set p2 [1 0 0 0 1 0 0 0])))

(let [b (metro-1)
      m 6
      r ( - m (mod b m))
      bmr (+ b r)]
  ;(println "actual beat is:     "b)
  ;(println "modulo value:       "m)
  ;(println "rest to zero in mod:"r)
  ;(println "zero mod beat:      "bmr "mod by" m " = "(mod bmr m))
  (at (metro-1 bmr) (do (seq-player metro-1 bmr @p2 p2 boom)
                        (seq-player metro-1 bmr @p1 p1 hatboom)))

                                        ;
  ;; (at (metro-1 (+ (metro-1) (- 4 (mod (metro-1) 4))))
  ;;                                       ;(seq-player metro-1 (metro-1) @hpat-1 hpat-1 boom)
  ;;     )
  )
