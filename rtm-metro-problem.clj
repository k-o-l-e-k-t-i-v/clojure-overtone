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
  [freq-mod 900
   freq-base 40]
  (let [e (env-gen (perc 0 0.02 1 0) 1 freq-mod freq-base 1)
        e2 (env-gen (perc 0 0.02 1 0) 1 (* 0.5 freq-mod) freq-base 1)
        a (env-gen (perc 0 0.2 1 0) 1 1 0 1)
        a2 (env-gen (perc 0 0.32 1 0) 1 1 0 1)

        s (sin-osc-fb e 0.5)
        s (distort (* 4 e s))
        s (resonz s (* 4 e) 0.5)
        sa (* s a)

        s2 (sin-osc e2)
        s2 (distort (* 40 e2 s2))
        s2 (rlpf s2 (* 4 e2) 0.15)
        sa2 (* s2 a2)

        sa3 (g-verb:ar sa2 30 0.93 0.95)

        o (mix [sa sa2 (* 0.23 sa3)])
        o (distort (* 5 o) )

        x (detect-silence o 0.001 0.5 FREE)]
    (out 0 (pan2 o 0 1))))

(boom)
(def metro-1 (metronome (* 3 160)))
(def p1 (ref [1 0 0 0 1 0 0 0 1 0 0 0 1 0 1 1]))
(dosync (ref-set p1 [1 1  1 1  1 1  1 1 ]))
(seq-player-x metro-1 (metro-1) @p1 p1 boom)
(stop)
(println "mind the gap")


(defn seq-player-x
  [metro beat pattern ref fnc]
  (let [t1 (metro beat)
        x  (choose [0])
        t2 (metro (+ x (inc beat)))
        p  (if (not (= () (rest pattern)))
             (rest pattern)
             (let [o @ref]
               o))
        g  (first pattern)]
    (at t1
        (if (= g 1)
          (let [r    (rand-int 20)
                prob -1]
            ;; (if (> r prob)                                        ;          (eval fnc)
            ;;   (fnc)
            ;;   (println "mind the gap"))
            (fnc)
            )))
    (apply-at t2 #'seq-player-x metro (+ x (inc beat)) p ref fnc [])))
(stop)
(def p2 (ref [1 0 1 0]))
(dosync (ref-set p2 [1 1 0 1 1 0 1 0 1 0 1 1 1 0]))

(def g1 (group))
(def g2 (group :after g1))

(def f1 (ref [1000 20 40 500 300 100 500 200]))
(def f2 (ref [100 30 30 40 20 40 400 40]))

(dosync (ref-set f2 [80 20 10 20
                     ]))

(dosync (ref-set f1 [2000 3000 2 3000 2000 1000 100 800]))

(defn bd []
  (boom [:head g1] :freq-mod (first @f1))
  (dosync (ref-set f1 (rotate 1 @f1))))

(defn bd2 []
  (boom [:head g1] :freq-base (first @f2))
  (dosync (ref-set f2 (rotate (rand-int 4) @f2))))

(print @f1)

(stop)
(bd2)
(do
  (seq-player-x metro-1 (metro-1) @p2 p2 bd)
  (seq-player-x metro-1 (metro-1) @p1 p1 bd2))
(metro-tock metro-1)
(defsynth rep
  []
  (let [s (in:ar 0 1)
        s2 (in:ar 1 1)
        fx (comb-l s 1 (* 0.000125  (metro-tock metro-1)) 1.5)
        fx2 (comb-l s2 1 (* 0.000125 (metro-tock metro-1)) 1.5)
        m (mix [s fx])
        m2 (mix [s fx2])]
    (replace-out:ar 0 m)
    (replace-out:ar 1 m2)))

(def rep1 (rep [:head g2]))
(kill rep1)
