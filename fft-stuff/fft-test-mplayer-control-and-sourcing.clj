(ns mplayer-control)
(use 'clojure.java.io)
(use 'late-night.core)
(use 'overtone.core)





(spit "/tmp/fifofile" "seek 23 2\npause\n")


(def m1 (metronome 180))
(defn posun []
  (let [x (+ 800 (first @p2))
        r (rand 100)]
    (dosync (ref-set p2 (rotate 1 @p2)))
    (do (spit "/tmp/fifofile" (str "seek " x " 2\n")))
    (if (> r 80)
      (freezer :l (choose [0.1 0.15 0.01])))
    ))
(posun)
(spit "/tmp/fifofile" "osd 0\n")
(odoc str)

(dosync (ref-set p2 (rotate )))
(stop)
(def p1 (ref [1 0 1 0 1 1 1 0 1 0 1 1 0 1 1 0]))
(at-zero-beat m1 1 p1 #'posun)
(metro-bpm m1 280)
(dosync (ref-set p1 [1 1 1 1]))
(def p2 (ref [630 628 624 482
              637 340 310 470
              ]))

(first @p2)




(def b1 (buffer (* 2 1024) 1))

(demo 1 (let [i (in:ar 8 1)]
          (out 0 (pan2 i 0 1))))

(defsynth monitor [v 1]
  (let [i (in:ar 8 1)]
    (out 0 (pan2 i 0 v))))

(def monitor-1 (monitor 0.2))

(ctl monitor-1 :v 0.5)
(stop)
(demo 20
      (let [b (local-buf (* 2 1024) 1)
            i (in:ar 8 1)
            xfft (fft b i)
            pv (pv-mag-freeze:kr xfft (lf-noise2:kr 3))
            o (ifft pv)
            o (g-verb o 20 0.2)
            o (freq-shift o -100)
            m (env-gen (perc 10 10 0.8 0)
                       1 1 0)
            ]
        (out 0 (pan2 o 0 m))))

(definst freezer [l 0.3]
  (let [b (local-buf (* 2 1024) 1)
        e (env-gen (perc 0 l 1 0)
                   1 1 0 FREE)
        i (in:ar 8 1)
        xfft (fft b i)
        pv (pv-mag-freeze xfft (impulse:kr 1))
        o (ifft pv)
        o (distort (* 40 o))
        o2 (comb-n o 1 (lin-lin (lf-noise0 1)
                               -1 1 0.001 0.005)
                 1 )
        o2 (normalizer o2 0.8)
        o (normalizer o 0.8)
        o (mix [o o2])
        o (* o e)
        ]
    (out 0 (pan2 o 0 1))))

(freezer :l 0.1)

