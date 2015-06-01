;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone

;(connect-external-server)
;(boot-external-server)



(defsynth buk
  []
  (out
   0 (pan2
      (free-verb
       (normalizer
        (apply + (allpass-c:ar
                  (* (decay (dust 0.8) 0.03 )
                     (freq-shift (sin-osc-fb:ar (+ (* 50 (lf-noise2:ar 1)) [30 500 1200]) 1.7) (* 100 (sin-osc-fb 1 1))))
                  0.3
                  (+ 0.2 (* 0.06 (lf-noise2:ar [1.4 2.8 3.1 6 8.7 13.1])))
                  0.9
                  )))
       0.87
       0.2
       0.9)
      (lf-noise2:ar 2))))
(buk)
(group "my-group-3" :head )

(def my-group-4 (group "my-group-4"))
(def my-group-5 (group "my-group-5"))

(def b1 (buk [:head my-group-4]))
(def b2 (buk [:head my-group-4]))
(def b3 (buk [:head my-group-4]))

(node-tree)
(kill buk)
(kill-server)
(defsynth fshi
  [freq   200
   freq2  80
   inbus1  0
   inbus2 1
   outbus 0
   ff1    2
   ff2    3]
  (let [insig1 (in:ar inbus1 1)
        insig2 (in:ar inbus2 1)
        shift1 (freq-shift:ar insig1 (* freq (lf-noise2:ar ff1)))
        shift2 (freq-shift:ar insig2 (* freq (lf-noise2:ar ff2)))
        rev1 (allpass-c:ar shift1)
        rev2 (allpass-c:ar shift2)]
    (out 0 [rev1   rev2  ])))

(def fshi1 (fshi [:head my-group-5] :ff1 0.2 :ff2 0.97))
(kill fshi1)
(stop)
