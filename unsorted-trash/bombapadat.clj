;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone

(stop)



(demo 30
      (out 0
           (pan2 (* (env-gen (envelope [0 1 1 0] [10 10 10]) 1 1 0 1)
                    (freq-shift (free-verb (* 0.5 (normalizer (+  (* (decay (impulse:kr 6 )
                                                                             0.2)
                                                                      (distort (* (+ 1
                                                                                     (* 100  (decay (impulse:kr 6)
                                                                                                    0.1)))
                                                                                  (sin-osc-fb (+ 6000  (* -4200 (decay (impulse:kr 6) 0.01))) 30))))
                                                                   (* (decay (impulse:kr 6 0.5)
                                                                             0.4)
                                                                      (distort (* (+ 1
                                                                                     (* 1000  (decay (impulse:kr 6 0.5)
                                                                                                     0.15)))
                                                                                  (sin-osc (+ 40  (* 5200 (decay (impulse:kr 6 0.5) 0.05)))))))
                                                                   (* (decay (impulse:kr 8 0.5)
                                                                             0.01)
                                                                      (* 2.5 (sin-osc-fb 2000
                                                                                         0.8)))
                                                                   (* (decay (impulse:kr 9)
                                                                             0.01)
                                                                      (* 1.5 (sin-osc-fb 2500
                                                                                         0.8)))
                                                                   (* (decay (impulse:kr 7/3 0.5)
                                                                             0.01)
                                                                      (* 5 (sin-osc-fb 4000
                                                                                       0.8))))))
                                            0.8
                                            0.9
                                            0.91)
                                (* 2000 (lf-noise2:ar (+ 6000 (* 4500.09 (lf-noise0:ar 3000))))) ))
                 (lf-noise2:ar 2000) 0.125)))

(stop)

(definst boomdrum
  [freqb 40
   freqm 100
   freqt 0.01
   fltb  200
   fltm  200
   fltt  0.05
   amp   1
   ampt  0.31
   dst   20
   dstt  0.05
   feed  0.15]
  (let [aenv (env-gen:ar (perc 0 ampt 20) 1 amp 0 1 FREE)
        fenv (env-gen:ar (perc 0.002 freqt 20) 1 freqm freqb 1)
        denv (env-gen:ar (perc 0 dstt 20) 1 dst 1 1)
        fltenv (env-gen (perc 0 fltt 10) 1 fltm fltb 1)
        src  (apply +  (sin-osc-fb:ar (* [0.5 1 2.32 1.193] fenv) feed))
        src  (distort (* denv src))
        src  (resonz src fltenv 0.3)
        src  (normalizer src)
        sig  (* aenv src)
        ]
    (out 0 (pan2 sig 0 amp))))


(defn bada [x]
  (cond (even? x) (boomdrum :amp 0.1 :ampt 0.5 :freqm 2000 :dstt 0.31 :freqt 0.03 :freqb (midi->hz (note :c1)))
        (odd? x) (do (boomdrum :amp 0.1 :ampt 0.25 :freqm 800 :dstt 0.031 :freqt 0.03 :freqb (midi->hz (note :c2)))
                     (cond (zero? (mod x 3)) (do (boomdrum :amp 0.11 :ampt 0.25 :freqm 8000 :dstt 0.031 :freqt 0.03 :freqb (midi->hz (note :c4)))
                                                  (println "fuck"))))
         ))


(bada 7)

(def m1 (metronome 320))
(m1)

(metro-bpm m1 2400)

(defn timak
  []
  (let [b (m1)
        bt (m1 b)
        bt2 (m1 (+ b 40))]
    (println (str "\nbeat number: " b "\nbeat timestamp: " bt "\nfour beats later:" bt2))
    (apply-at bt2 (println (str "kokoro")))
    ))

(timak)

(println (str ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;"))
(defn badartm [m t x r]
  (let [b1 (m t)
        b2 (m (+ t x))]
    (at b1 (bada r))
    (apply-at b2 badartm m (+ x t (* 12 (rand-int 2))) x (inc r) [])
    )
  )
(stop)
(badartm m1 (m1) 2 0)
