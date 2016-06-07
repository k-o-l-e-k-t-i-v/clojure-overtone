(def metro-1 (metronome 560))
(metro-1)

;(at (metro-1 (+ 8 (metro-1))) (println "kokos"))

;(println "kokos")

;(metro-1 (+ 8 (metro-1)))

(def patt-1 (ref [1 1 1 1 1 1 1 1]))
(def patt-2 (ref [1 0 0 1 0 1 1 0 1 1 0 1 0 1 0]))
(def patt-3 (ref [1 0 0 0 0 0 0 0 0 1 0 0 0 1 0 0 0 0 0 1 0 0 0 0 0]))
(defn seq-player
  [metro beat pattern ref fnc]
  (let [t1 (metro beat)
        t2 (metro (inc beat))
        p  (rest pattern)
        g  (first pattern)]
    (at t1
        (if (= g 1)
          (fnc)))

    (if (not (= () p))
      (apply-at t2 #'seq-player metro (inc beat) p ref fnc [])
      (apply-at t2 #'seq-player metro (inc beat) @ref ref fnc []))
    ))

(do
  (seq-player metro-1 (metro-1) @patt-1 patt-1 beep)
  (seq-player metro-1 (metro-1) @patt-2 patt-2 hs)
  (seq-player metro-1 (metro-1) @patt-3 patt-3 gbas))
(stop)

(print @patt-1)
(do
  (dosync (ref-set patt-1 [1 1 1 1 1 0 1 1 1 0 1 0]))
  (dosync (ref-set patt-2 [1 0 1 1 1 0]))
  (dosync (ref-set patt-3 [1 0 1 0 1 0 1 0 0 0 0 0 1 0 0 0])))
(metro-bpm metro-1 (* 2 280))
(metro-bpb metro-1 (* 2 280))
(stop)
(dosync (ref-set patt-1 [1 0 0 0 0 0 0 1 0 0 0 0]))
(dosync (ref-set patt-2 [1 0 0 0 0 0 0 0 0]))
(dosync (ref-set patt-3 [1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]))

(deref patt-1)
(print @patt-1)



(stop-player seq-player)

(defsynth beep
  []
  (let [m (demand (impulse 1/32) 0 (drand [60 50 40 50 60] INF))
        m (* 0.02 (midicps m))
        f (env-gen (perc 0 0.01 1 0) 1 1000 40 1)
        s (sin-osc (+ m f))
        e (env-gen (perc 0 0.13 1 0) 1 1 0 1 FREE)

        se (* s e)
        se (distort (* (* 0.2 f) se))]
    (out 0 (pan2 se))))
(beep)

(defsynth hs
  []
  (let [
        f (env-gen (perc 0 0.01 1 0) 1 60 4000 1)
        s (white-noise)
        s (rlpf s (+ (* 4000 (lf-noise2 3)) f) 0.9)
        s (free-verb s 0.3 0.4 0.9)
        s (normalizer s)
        e (env-gen (perc 0 0.0533 1 0) 1 1 0 1 FREE)
        se (* s e)
;        se (comb-n se 2 1/4 1.3)
        se (distort (* 6 se))
        ]
    (out 0 (pan2 se))))

(defsynth gbas
  []
  (let [
        f (env-gen (perc 0 0.1 1 0) 1 100 40 1)
        s (sin-osc-fb f 0.5)
        s (freq-shift s (env-gen (perc 0 0.1 1 0) 1 200 -100 1))
        e (env-gen (perc 0 0.33 1 0) 1 1 0 1)
        se (* s e)
        fx (g-verb se 20 5 0.1)
        swe (mix [fx se])
        se (freq-shift se (env-gen (perc 0 0.1 1 0) (impulse 2) (demand (impulse 1/32) 0 (drand [40 0 90 0 50] INF)) 40 1))
        ds (demand (trig 1 0.01) 0 (drand [9 9 9 12 16] INF))
        se (distort (* ds se))
        x (detect-silence:ar se 0.01 2 FREE)
        ]
    (out 0 (pan2 se))))
(gbas)
(stop)
(odoc trig)
(defn mel-player
  [metro beat pattern ref fnc]
  (let [t1 (metro beat)
        t2 (metro (inc beat))
        p  (rest pattern)
        g  (first pattern)]
    (at t1
        (if (not (= g 0))
          (fnc g)))
     (if (not (= () p))
      (apply-at t2 #'mel-player metro (inc beat) p ref fnc [])
      (apply-at t2 #'mel-player metro (inc beat) @ref ref fnc [])
       )
    ))
;(def mel-1 (ref [40 0 0 0 49 0 0 0 47 0 0 0 51 0 0 0]))
(dosync (ref-set mel-1 [90 0 0 0
                        0 0 0 0
                        ]))
(stop)

(defsynth melos
  [nota 0]
  (let [f (env-gen (perc 0 0.02 1 0) 1 8000 0 1)
        m (midicps nota)
        fm (+ f m)
        s (lf-noise2 fm)
        s (wrap:ar s -0.13 0.93)
        s (resonz s (env-gen (perc 0 0.01 1 10) 1 -6200 12000 1) 2)
        s (normalizer s)
        e (env-gen (perc 0  0.127 1 0) 1 1 0 1)
        se (* s e)
        ;se  (g-verb se 120 4 0.18)
        x (detect-silence se 0.01 1 FREE)
        ]
    (out 0 (pan2 se (* 0.5 (lf-noise2 1/3)) 0.24))))

(melos 40)
(mel-player metro-1 (metro-1) @mel-1 mel-1 melos)
(stop-player mel-player)

(defsynth hhat
  []
  (let [f (env-gen (perc 0 0.05 1 -10) 1 -1300 11400 1)
        s (lf-noise1 f)
        e (env-gen (perc 0 0.01 1 0) 1 1 0 1 FREE)
        se (* s e)
        ]
    (out 0 (pan2 se))))

(hhat)

(hhat)
(def patt-hat-1 (ref [1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]))
(dosync (ref-set patt-hat-1 [1 0 1 0 1 0 1 0]))

(stop)
(do
  (dosync (ref-set patt-1
                   [1 1 0
                    1 1 0 1 1
                    0 1 0 1
                    1 0 ]))
  (dosync (ref-set patt-2
                   [1 0 1 0
                    1 0 1 0
                    1 1 1 0
                      ]))
  (dosync (ref-set patt-3
                   [1 1 1 0
                    1 1 1 0
                    1 1 1 0]))
  (dosync (ref-set patt-hat-1
                   [1 1 0 1 1
                    1 0 1 1 0]))
  (dosync (ref-set mel-1 [50 0
                          90 0 0
                          30 0 0
                          90 0 70])))
(stop)


(do
  (mel-player metro-1 (metro-1) @mel-1 mel-1 melos)
  (seq-player metro-1 (metro-1) @patt-hat-1 patt-hat-1 hhat)
  (seq-player metro-1 (metro-1) @patt-1 patt-1 beep)
  (seq-player metro-1 (metro-1) @patt-2 patt-2 hs)
  (seq-player metro-1 (metro-1) @patt-3 patt-3 gbas))

(stop-player 14131)
(stop)


(mel-player metro-1 (metro-1) @mel-1 mel-1 melos)
