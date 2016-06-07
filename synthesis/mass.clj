(do (use 'overtone.core)
    (use 'late-night.core))

(demo 10
      (let [i (in:ar 10)
            i (rlpf i 4000 0.3)
            i (normalizer i)
            t1 (impulse 1)
            d1 (decay t1 0.12)
            d2 (* 7000 (decay t1 0.05))
            s (sin-osc d2)
            s (+ s i)
            s (* s d1)
            ]
        (out 0 (pan2 s))
        (out 4 s)))

(demo 10
      (let [i (in:ar 10)
            i (rlpf i 60 0.999)
            i (normalizer i)
            i (distort (* 5 i))
            i (* (decay (impulse 2) 0.41) i)]
        (out 0 i)
        (out 4 i)))

(definst bd-drt-1 [a 1]
      (let [i (in:ar 11)
            i (normalizer i)
            i (freq-shift i 1000)
            s (sin-osc (env-gen (perc 0 0.01 1 -5)
                                1 4000 30 1))
            s (distort (* 3 s))
            s (* s (env-gen (perc 0 0.3 1 0)
                            1 1 0 1))
            i (+ s (* (* 1 i) (freq-shift (comb-c i 0.06 (lin-lin (lf-noise2 0.3) -1 1 0.05 0.001) 1)
                                          50))
                 )

            oe (env-gen (perc 0 2 a -10)
                        1 1 0 1 FREE)
           ; i (g-verb i 100 8.2)
            o (* oe i)
            o2 (round o (lf-noise2 13))
            o (round o (lf-noise2 7))
         ;   o (g-verb o 120 1.2)
         ;   o2 (g-verb o2 120 1.4)


            ]
        (out 0 (* oe o) )
        (out 1 (* oe o2))
        (out 4 (* oe o))
        (out 5 (* oe o2))
        ))
(stop)

(bd-drt-1)
(def p1 (ref [1 0 1 1 0 1 1 0 1 1 0 1 0 1 0 1 1 0]))
(def m1 (metronome 360))

(defn k []
  (bd-drt-1 [:head g-1] :a 1))


(at-zero-beat m1 8 p1 #'k)

(def g-1 (group ))
(def g-2 (group :after g-1))

(definst verba [a 1 b 1 c 1 d 1]
  (let [i (in:ar 0)
        v (g-verb i 100 4)]
    (out 0  (* v (lpf a 2)))
    (out 1  (* v (lpf b 2)))
    (out 4  (* v (lpf c 2)))
    (out 5  (* v (lpf d 2)))))

(verba [:head g-2] :a 0 :b 0 :c 0 :d 0)
(ctl verba :a 0.4 :b 0.4)

(dosync (ref-set p1 [1 0 1 0 1 0 1 0 1 1 0 1 1 0 1 1 0 ]))
(dosync (ref-set p1 [1 0 1 1 0 1 1 0 1 1 0 1 0 1 0 1 1 0]))
(stop)
