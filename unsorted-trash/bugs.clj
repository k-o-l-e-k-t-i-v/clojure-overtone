;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone




(use 'overtone.core)

(stop)

(demo 100
      (out 0 (+
                 (pan2
                  (* (pow  (lf-noise2:ar 3)
                        2)
                     (free-verb (freq-shift  (normalizer (apply + (grain-sin:ar 1
                                                                                (impulse:kr (into [] (repeatedly 3 (fn [] (* 1 (+ 2 (rand 13)))))))
                                                                                1.2
                                                                                (+ 420 (* (apply + (lf-noise2:ar (repeatedly 5 (fn [] (+ 0.2 (rand 6)))))) 200))
                                                                                0
                                                                                -1
                                                                                30)))
                                             400)
                                0.83 0.997 0.81)
                     )
                  (lf-noise2:ar 1)
                  (+ 0.5 (* 0.5 (pow (lf-noise2:ar 0.1) 1.63))))

                 (pan2
                  (* (pow  (lf-noise2:ar 3)
                        2)
                     (free-verb (freq-shift  (normalizer (apply + (grain-sin:ar 1
                                                                                (impulse:kr (into [] (repeatedly 8 (fn [] (* 1 (+ 2 (rand 13)))))))
                                                                                1.2
                                                                                (+ 420 (* (apply + (lf-noise2:ar (repeatedly 8 (fn [] (+ 0.2 (rand 6)))))) 200))
                                                                                0
                                                                                -1
                                                                                30)))
                                             200)
                                0.83 0.997 0.81)
                     )
                  (lf-noise2:ar 1)
                  (+ 0.5 (* 0.5 (pow (lf-noise2:ar 0.1) 3))))

                 (pan2
                  (* (pow  (lf-noise2:ar 3)
                        2)
                     (normalizer  (rlpf (free-verb (freq-shift  (normalizer (sin-osc-fb (* 1000 (lf-noise2:ar 70))))
                                                                1400)
                                                   0.83 0.997 0.81)
                                        400 0.96))
                     )
                  (lf-noise2:ar 1)
                  (+ 0.5 (* 0.5 (pow (lf-noise2:ar 0.1) 1.3))))

                 (pan2 (distort (* (* (pow  (lf-noise2:ar 20) 3) 10) (sin-osc 40))) (lf-noise2:ar 2) (+ 0.5 (* 0.5 (pow (lf-noise2:ar 1) 3))))
                 (pan2 (distort (* (* (pow  (lf-noise2:ar 20) 4) 10) (sin-osc 30))) (lf-noise2:ar 1) (+ 0.5 (* 0.5 (pow (lf-noise2:ar 1) 3))))

                 (pan2 (freq-shift  (comb-c  (* (pulse:ar (* 10 (lf-noise2:ar 8)) (+ 0.5 (* 0.5 (lf-noise2:ar 1))))
                                                (sin-osc-fb 80 0.8))
                                             4
                                             (+ 0.02 (+ 0.02 (* 0.02 (pow (lf-noise2:ar 10) 4))))
                                             0.99)
                                    8000)
                       (* 0.4 (lf-noise2:ar 10))
                       0.01)

                 (pan2 (free-verb  (* (pow (lf-noise2:ar 10) 3)
                                      (normalizer (apply + (formant:ar [40 50 900 1230] (* 800 (lf-noise2:ar 30)) 200))))
                                   0.93
                                   0.99
                                   0.9) -1
                                   (+ 0.5 (* 0.5 (pow (lf-noise2:ar 0.1) 1.4))))

                 (pan2 (free-verb  (* (pow (lf-noise2:ar 10) 3)
                                      (normalizer (apply + (formant:ar [42 56 950 1330] (* 880 (lf-noise2:ar 20)) 200))))
                                   0.98
                                   0.995
                                   0.9) 1
                                   (+ 0.5 (* 0.5 (pow (lf-noise2:ar 0.1) 1.2))))

                 (pan2 (apply +  (* (pow (lf-noise2:ar [3 7 10]) 3)
                                    (normalizer (distort (* 20 (apply + (formant:ar [42 56 95 133] (* 8800 (lf-noise2:ar 2)) (+ 1300 (* (lf-noise2:ar 4) 200)))))))))
                       (* 0.3  (lf-noise2:ar 0.5))
                       0.23))))

(stop)


















(stop)

(demo 80 (out 0 (pan2 (formant:ar 40 (* 800 (lf-noise2:ar 30)) 200))))




(grain-sin:ar 1 (impulse:kr 2) 0.2 (+ 6000 (* (sin-osc 20) 1000)) 0 -1 10)

(grain)







;; SLOVICKA
;;
;; (grain-sin:ar)
