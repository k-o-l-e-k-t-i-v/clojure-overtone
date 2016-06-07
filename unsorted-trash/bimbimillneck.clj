(def pat-1 (ref [1 0 0 1 0 0 1 0]))
(dosync (ref-set pat-1 [1 0 0 1 0]))
(def pat-2 (ref [1 0 0 1 0 0 1 0]))
(def pat-3 (ref [1 0 1 0 0 1 0 1 0 0 0 1 0]))

(dosync (ref-set pat-1 [1 0 1 0 0 1 0])
        (ref-set pat-2 [1 0 0 1 0 1])
        (ref-set pat-3 [0 0 1 0 0 1 0 1 0 0 1 0]))
(do
  (dosync (ref-set pat-1 [1 0 1 0 1 0 0 1 0]))
  (dosync (ref-set pat-2 [1 0 1 0 1 0]))
  (dosync (ref-set pat-3 [0 0 1 0 1 1 0 0 1 1 0 1 0])))

(println @pat-1)

(use 'overtone.core)

(def m-1 (metronome 400))

(definst kick-1
  [freq-mod 1000
   freq-base 50
   t1 0.3
   t2 0.1]
  (let [fenv (env-gen:ar (perc 0 t2 freq-mod -10) 1 1 freq-base 1)
        amp (env-gen (perc 0 t1 1 -10) 1 1 0 1)
        s (square fenv)
        s (rlpf s (* 1 fenv) 0.5)
        s (g-verb s :roomsize 90 :revtime 90 :maxroomsize 120 :taillevel 0.9)
        s (freq-shift s (lin-lin (lf-noise2 4000) 0 1 -200 200))
        sa (* s amp)
        sil (detect-silence sa 0.001 0.1 FREE)]
    (out 0 (pan2 sa 0 1))))

(defn kicka []
  (kick-1))
(defn kicka-2 []
  (kick-1 :freq-base 380 :freq-mod 1600))
(defn kicka-3 []
  (kick-1 :freq-base 190 :freq-mod 1200 :t2 0.02 :t1 0.6))

(use 'late-night.core)

(let [zb (zero-beat m-1 8)]
  (at (m-1 zb)
      (seq-player m-1 zb @pat-1 pat-1 #'kicka)))

(let [zb (zero-beat m-1 8)]
  (at (m-1 zb)
      (seq-player m-1 zb @pat-2 pat-2 #'kicka-2)))

(let [zb (zero-beat m-1 8)]
  (at (m-1 zb)
      (seq-player m-1 zb @pat-3 pat-3 #'kicka-3)))

(stop)
