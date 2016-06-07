(do (use 'overtone.core)
    (use 'late-night.core))



(defn grow [freqs t]
  (let [nt (+ t 100)]
    (demo 100 (let [s (sin-osc (first freqs))]
                (out 0 (pan2 s 0 0.001))))
    (if (not (empty? freqs))
        (apply-at nt #'grow (shuffle (rest freqs)) nt))
    ))


(def freqs (range 20 20000))
(print @freqs)

(grow @freqs (now))
(stop)
(def freqs (ref (into [] (range 20 20000))))

(defn koks []
  (let [f (shuffle @freqs)]
    (demo 1000
          (let [s (sin-osc (first f))]
            (out 0 (pan2 s 0 0.001))))
    (dosync (ref-set freqs (into [] (rest f))))))
(dotimes [n 1000]
  (koks)
  (Thread/sleep 200))
(def p-1 (ref [1 1 1 1]))
(def m-2 (metronome 120))
(at-zero-beat m-2 1 p-1 #'koks)
(stop)
