;; Version 1.0beta26
;;
;;           http://github.com/overtone/emacs-live
;;
;;


(boot-external-server)
(demo 200
      (let [t  (impulse:ar 1/4)
            tr (impulse:ar (+ 1 (* 3.3 (lf-noise0 2))))
            tr2 (impulse:ar (+ 1 (* 3.3 (lf-noise0 2))))
            counta   (pulse-count:ar tr 16)
            ]
        (out 0
             (pan2 (free-verb (* (env-gen:ar (perc 0.2 0.8) t 1 0 1)
                                 (env-gen:ar (perc 0.26 0.29 1 2) tr 0.93 0 1)
                                 (sin-osc:ar (+ (* 200 (decay tr 0.2))
                                                (* 100 (decay tr2 0.2))
                                                (* 2500 (lf-noise2 4)) 3156))
                                 )

                              0.359 0.966 0.93)))))
(stop)
