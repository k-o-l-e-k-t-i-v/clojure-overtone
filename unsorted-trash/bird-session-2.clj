;; Version 1.0beta26
;;
;;           http://github.com/overtone/emacs-live
;;
;;



(dotimes [n 2]
  (demo 200
        (let [tr (impulse:ar (* 2.3 (lf-noise0 0.3)))
              counta   (pulse-count:ar tr 16)
              ]
          (out 0
               (pan2 (free-verb (* (env-gen:ar (perc 0.06 0.29 1 2) tr (rand 0.6) 0 1)
                                   (sin-osc:ar (+ (* 2000 (decay tr 0.2)) (* 500 (lf-noise2 30)) 3156))
                                   )

                                0.49 0.36 0.3))))))
(stop)
