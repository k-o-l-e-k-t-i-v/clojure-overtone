(use 'overtone.core)
(use 'late-night.core)

(def m (metronome 320))

(def p (ref [30 44 50 60]))

(defn w []
  (let [t (first @p)
        t2 (+ 690 t)]
    (spit "/tmp/mpf" (str "seek " t2 " 2\n"))
    ;; (if (= t 0)
    ;;   (ctl in-verb-1 :t 4)
    ;;   (ctl in-verb-1 :t 2.3))
    (dosync (ref-set p (rotate 1 @p)))))
;(spit "/tmp/mpf" "seek 20 2\n")

(w)

(at-zero-beat m 8 p #'w)

(dosync (ref-set p [1 0 1 2 ]))
(metro-bpm m 120)
