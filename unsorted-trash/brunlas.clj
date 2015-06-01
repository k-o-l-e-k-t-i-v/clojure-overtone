;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone


(taran)

(definst taran
  []
  (out 0
        (pan2 (free-verb (* ;(decay2 (impulse (choose [1/3 1/2 1 2])) 0.2 0.623)
                          (env-gen (envelope [0 1 1 0]
                                             [5 5 5])
                                   1
                                   1
                                   0
                                   1
                                   FREE)
                          (normalizer (distort (* (* 2000
                                                     (lf-noise2:ar (rand 1)))
                                                  (resonz (distort (* (lin-exp (lf-noise2:ar (rand 4))
                                                                               -1
                                                                               1
                                                                               1
                                                                               6000)
                                                                      (sin-osc-fb (+ 90
                                                                                     (* 10
                                                                                        (lf-noise2:ar (rand 0.14))))
                                                                                  (+ 0.5 (* 0.5
                                                                                            (lf-noise2:ar (rand 10)))))))
                                                          (lin-exp (lf-noise2:ar (rand 4))
                                                                   -1
                                                                   1
                                                                   50
                                                                   2000)
                                                          9.9)))))
                         0.243
                         0.80
                         0.0942)
              (* 0.97
                 (lf-noise2:ar 0.72))
              0.15)))

(def continue? 1)
(str continue?)
(defn taran-metro
  [t]
  (let [n (now)
        c (pos? continue?)]
    (at n (taran))
    (while c
      (apply-at (+ n t) taran-metro t []))))

(pos? continue?)
(taran-metro 1000)
(taran)
(stop)
