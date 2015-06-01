;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone

(demo 30
      (let [src (normalizer (apply + (sin-osc-fb (into [] (repeatedly 10 (fn [] (+ 400 (rand 120))))) 0.2)))
            amp (pow (lf-noise2:ar 2) 3)
            sig (* amp src)
            env (env-gen (envelope [0 1 1 0] [5 20 5]) 1 1 0 1)
            sig1 (free-verb (freq-shift:ar sig 210) 0.9 0.9 0.1)
            sig1 (* sig1 env)
            sig2 (free-verb (freq-shift:ar sig 200) 0.9 0.9 0.1)
            sig2 (* sig2 env)
            sig3 (* (pow  (lf-noise2:ar 4) 4) (distort (* 20 (sin-osc-fb 40 0.4))))

            sig3 (normalizer sig3)
            sig3 (* env sig3)]
        (out 0 (+ (pan2 sig1 (lin-lin (lf-noise2:ar 2) -1 1 -0.3 -1))
                  (pan2 sig2 (lin-lin (lf-noise2:ar 2) -1 1 0.3 1))
                  (pan2 sig3 0)))))

(stop)
