;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone

                                        ;(demo 10 (sin-osc 200))

(definst hardkick
  [freq-base 60
   freq-acc  2000
   freq-t    0.02
   amp       1
   amp-t     0.2
   dist      0.5]
  (let [t        (impulse:kr 1/40)
        amp-env  (decay t amp-t)
        silens   (detect-silence amp-env 0.001 1 FREE)
        freq-env (+ freq-base (* freq-acc (decay t freq-t)))
        sig-src  (sin-osc freq-env)
        sig-amp  (* amp-env sig-src)
        sig-dist (distort (* freq-env sig-amp))
        ]
    (out 0 (pan2 sig-dist))))

(definst hardhat
  [freq-base 8000
   freq-acc  2000
   flt-base  400
   freq-t    0.02
   amp-t     0.1
   amp       1]
  (let [t       (impulse:kr 1/40)
        amp-env (decay t amp-t)
        silens  (detect-silence amp-env 0.001 1 FREE)
        freq-env (+ freq-base (* freq-acc (decay t freq-t)))
        noise-env (* freq-acc (lf-noise0 100))
        sig-src  (mix [(white-noise) (formant:ar freq-env noise-env noise-env)])
        sig-src  (bpf sig-src freq-env 0.9)
        sig-src  (distort (* 20 sig-src))
        sig-src  (* amp-env sig-src)]
    (out 0 (pan2 sig-src))))

(definst hardsnare
  [freq-base 30
   freq-acc  7500
   freq-t    0.1
   amp-t     0.1
   amp       1
   dist      0.2]
  (let [amp-env  (env-gen (perc 0 amp-t amp 0) 1 1 0 1 FREE)
        freq-env (env-gen (perc 0 freq-t freq-acc -20) 1 1 freq-base 1)
        sig-src  (mix [(lf-noise2:ar (* 2 freq-env)) (square freq-env) (white-noise)])
        sig-src  (rhpf:ar sig-src freq-env 0.18)
        sig-dist (distort (* (* dist freq-env) sig-src))
        sig-src  (* amp-env sig-dist)
        ]
    (out 0 (pan2 sig-src))))


(hardsnare)
(hardkick)
(hardhat :freq-base 10000)
