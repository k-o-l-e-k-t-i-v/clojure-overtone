;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone
(demo 60 (out 0 (pan2 (* (env-gen (envelope [0 1 1 0] [5 20 8]))
                         (+ 0.25 (* 0.25 (sin-osc 400)))
                         (apply + (normalizer (rlpf (square (+ (* 0.1 (lf-noise2:ar 2)) (* (note :g4) [0.5 1 1.1 2.09 1.973]))) (+ 500 (* 150 (sin-osc 50))) 3)))))))



(demo 100
      (out 0
           (pan2 (* (+ 0.5 (* 0.5 (sin-osc 800)))
                    (freq-shift:ar (free-verb  (allpass-c:ar (rlpf  (*
                                                                     (decay:ar (impulse:ar (+ 1 (* 0.63 (lf-noise2:ar 0.7))) )
                                                                               0.13)
                                                                     (sin-osc-fb (+ 2000 (* 60 (lf-noise2:ar 2)))
                                                                                 0.972))
                                                                    2000
                                                                    2.6)
                                                             2
                                                             (+ 1.2 (* 0.8 (lf-noise2:ar 3)))
                                                             1.2)
                                               0.39
                                               0.97
                                               0.9)
                                   (* 800 (decay (dust 3) 0.5))))
                 (lf-noise2:ar 1)
                 1)))

(stop)
(kill-server)
(defsynth simona
  [freq 100
   amp  0.2
   t    2]
  (let [src (sin-osc-fb freq 0.4)
        amp (env-gen (envelope [0 amp amp 0] [0.6 t 1.2]) 1 1 0 1 FREE)
        sig (* amp src)]
    (out 0 (pan2 sig 0 1))))
(def x (simona :freq 120))

(demo 40 (out 0 (pan2 (freq-shift:ar (free-verb (rlpf (* (env-gen (envelope [0 1 1 0] [2 6 0.3]) 1 1 0 1)
                                                                      (decay (impulse:kr (line:kr 0.2 (+ 7 (rand 20)) 8)) 0.022)
                                                                      (sin-osc-fb (+ 900 (* 16 (lf-noise2:ar 3))) 0.08))
                                                                   3000
                                                                   0.6)
                                                0.39
                                                0.297
                                                0.92) -100) (lf-noise2:ar 2) 1)))

(connect-external-server 94.112.205.204 57110)
(connect-external-server "94.112.205.204" 57110)

(boot-external-server)

(node-tree)
