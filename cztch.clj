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

(def metro-1 (metronome 320))



(defsynth boom
  []
  (let [ae (env-gen (perc 0 0.11 1 0) 1 1 0 1 FREE)
        fb (demand (impulse 1/30) 0 (drand [2000 400 1200 0]))
        fe (env-gen (perc 0 0.02 1 0) 1 fb 40 1)
        s (sin-osc-fb fe 1.5)
        n (white-noise)
        s (mix [s n])
        s (* s ae)
        s (distort (* 10 s))
        s (rlpf s (* 2 fe) 0.4)
        s (distort (* 5 s))
        sfx (g-verb s 100 30 0.1)
        s (mix [s (* 0 sfx)])
        s (freq-shift s (demand (impulse 1) 0 (drand [-100 30 20])))
        w (demand (impulse 1/32) 0 (drand [1]))
        x (normalizer (wrap:ar s -0.24 0.24))
        x (freq-shift x -80)
        s (select:ar w [s x])
        s (freq-shift s 0)
;        s (* s (decay (impulse 4) 0.1))
;        s (comb-n s 1)
        ]
    (out 0 (pan2 s 0 1))))
(boom)
(stop)
(defn seq-player
  [metro beat pattern ref fnc]
  (let [t1 (metro beat)
        x (choose [0])
        t2 (metro (+ x
                     (inc beat)))

        p  (if (not (= () (rest pattern)))
             (rest pattern)
             (let [o @ref
                   c (count o)
                   t (take (+ 2 (rand-int (- c 2))) o)]
               o))
        g  (first pattern)]
    (at t1
        (if (= g 1)
          (let [r (rand-int 20)]
            (if (> r 3) (fnc)))
          ))
    (apply-at t2 #'seq-player metro (+ x (inc beat)) p ref fnc [])
    ))

(def pat-1 (ref [1 0 0 1 0 0 1 0]))
(stop)
(dosync (ref-set pat-1 [1 0 0 0 0 0
                        ]))

(seq-player metro-1 (metro-1) @pat-1 pat-1 boom)

(def pat-2 (ref [1 0 0 1 0 1 0]))

(seq-player metro-1 (metro-1) @pat-2 pat-2 beep)
(def pat-3 (ref [1 1 0 0 1 1 1 0]))
(def pat-4 (ref [1 1 0 0 1 1 1 1 1 0]))
(do
  (seq-player metro-1 (metro-1) @pat-1 pat-1 boom)
  (seq-player metro-1 (metro-1) @pat-2 pat-2 beep)
  (seq-player metro-1 (metro-1) @pat-3 pat-3 beep)
  (seq-player metro-1 (metro-1) @pat-4 pat-4 hs))
(do
  (dosync (ref-set pat-1 [1 0 1 0]))
  (dosync (ref-set pat-2 [1 0 0 1]))
  (dosync (ref-set pat-3 [1 0 0]))
  (dosync (ref-set pat-4 [1 0 0 0 0 1 0])))


(metro-bpm metro-1 (* 3 160))
(metro-bpm metro-1 0)
(stop)
(defsynth hs
  []
  (let [
        f (env-gen (perc 0 0.01 1 0) 1 60 400 1)
        s (white-noise)
        s (rhpf s (+ (* 400 (lf-noise2 3)) f) 0.9)
        s (free-verb s 0.13 0.4 0.9)
        s (normalizer s)
        s (freq-shift s 500)
        e (env-gen (perc 0 0.0533 1 0) 1 1 0 1 FREE)
        se (* s e)
        se (freq-shift se 300)
;        se (comb-n se 2 1/4 1.3)
        se (distort (* 6 se))
        se (freq-shift se 60)
        ]
    (out 0 (pan2 se))))
(defsynth beep
  []
  (let [m (demand (impulse 1/32) 0 (drand [60 50 40 50 60] INF))
        m (* 0.02 (midicps m))
        f (env-gen (perc 0 0.01 1 0) 1 600 20 1)
        s (sin-osc (+ m f))
        e (env-gen (perc 0 0.3 1 0) 1 1 0 1 FREE)

        se (* s e)
        se (distort (* (* 0.4 f) se))
        se (freq-shift se 40)]

    (out 0 (pan2 se))))
