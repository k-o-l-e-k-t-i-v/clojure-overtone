;; Version 1.0beta26
;;
;;           http://github.com/overtone/emacs-live
;;
;;



(demo 20
      (let [tr (impulse:ar (+ 1 (* 4.3 (lf-noise0 3))))
            counta   (pulse-count:ar tr 16)
            ]
        (out 0
             (pan2 (free-verb (* (env-gen:ar (perc 0.26 0.29 1 2) tr 0.93 0 1)
                                 (sin-osc:ar (+ (* 2000 (decay tr 0.2)) (* 2500 (lf-noise2 40)) 3156))
                                 )

                              0.859 0.466 0.93)))))
