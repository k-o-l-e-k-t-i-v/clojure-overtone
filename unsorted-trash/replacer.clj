;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone
(boot-external-server)
(stop)
(kill-server)

(demo 1000
      (let [a (decay (impulse:ar (+ 4 (* 3 (lf-noise2:ar 0.4)))) 0.2)
            s (formant:ar (+ 800  (* 300 (+ (sin-osc-fb 200 0.9) (lf-noise2:ar 0.2)))) (+ 400  (* 300 (lf-noise2:ar 0.2))) (+ 400  (* 3000 (lf-noise2:ar 0.2))))
            s (* s a)
            s (free-verb s 0.93 0.98 0.9)
            s2 (formant:ar (+ 300 (* 200 (sin-osc-fb:ar 0.2 0.6)))
                           (+ 400 (* 10 (lf-noise2:ar 0.2)))
                           (+ 900 (* 10  (saw:ar (+ 0.30 (lf-noise2:ar 1))) )))
            s2 (freq-shift s2 (* 6000 a (lf-noise2:ar 2)))
            s (* 0.2 (+ s s2))
            p (* 0.24 (sin-osc (* 20 (lf-noise2:ar 0.3))))]
        (out 0 (pan2 s p))))


(demo 1000 (let [i (in:ar 0 1)
                 i2 (in:ar 1 1)
                 o (* i (+ (decay (impulse:ar (* (lf-noise2:ar 1) 4)) 0.2) (decay (impulse:kr 10) 0.1)))
                 o2 (* i (+ (decay (impulse:ar (* (lf-noise2:ar 3) 8)) 0.2) (decay (impulse:kr 8) 0.1)))
                 r1  (normalizer (free-verb o 0.9 0.1 0.9))
                 r2  (normalizer (free-verb o2 0.9 0.9 0.2))]
           (replace-out:ar 0 r1)
           (replace-out:ar 1 r2)))

(demo 1000 (let [i1 (in:ar 0 1)
l                 i2 (in:ar 1 1)
                 s1 (freq-shift i1 (- 22 (* 20 (lf-noise2:ar 2))))
                 s2 (freq-shift i2 (+ 20 (* 20 (lf-noise2:ar 2))))
                 r1 (normalizer (resonz s1 (+ 2000 (* 1500 (lf-noise2:ar 200))) 3.9))
                 r2 (normalizer (free-verb s2 0.9 0.9 0.2))
                 r1 (* (pow (lf-noise2:ar 5) 3) r1)
                 r2 (* (pow (lf-noise2:ar 6) 3) r2)
                 r1  (free-verb r1 0.9 0.1 0.9)
                 r2  (free-verb r2 0.9 0.9 0.2)]
      (replace-out:ar 0 r2)
      (replace-out:ar 1 r1)) )

(stop)
