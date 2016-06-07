(use 'overtone.core)
(use 'late-night.core)



(demo 10 (out 0 (pan2 (distort (* 4 (g-verb (* (decay (impulse:ar 2) 0.03)
                                                (sin-osc-fb (+ 520 (* 200 (lf-noise2 3))) 2))
                                            :roomsize 90 :revtime 4 :drylevel 1))))))

(demo 10 (out 0 (pan2 (* (env-gen (perc 3 7 20) 1 1 0 )
                         (* (decay (impulse 3) 0.05)
                            (sin-osc-fb (midicps  (+ 20
                                                     (demand (impulse 5) 0 (dser [1 3 1 4 1 7 5 4 3] 200))
                                                     (demand (impulse 1/3) 0 (dser [40 52 40 47] 200))))))))))





(demo 30 (let [m (demand (impulse (demand (impulse 2) 0 (drand [8 12 16 9 6 1/2 1/4] 2000))) 0 (dser [1 0 1 0 1 0 1 0] 2000))
               m2 (demand (impulse (demand (impulse 2) 0 (drand [8 3 5 1] 2000))) 0 (drand [ 300 4000 300 500 700] 3000))
               en (env-gen (perc 0 (lin-lin:kr (lf-noise2:kr 4) -1 1 0.01 0.2) 1 -5) m 1 0 1)
               s (distort (* 30 (+
                                    (free-verb (freq-shift  (sin-osc m2)
                                                            (lin-exp (lf-noise2 5) -1 1 0 400)
                                                            (lin-lin (lf-noise2 4) -1 1 0 4000))
                                               0.3 0.9 0.1))))
               se (* s en)
               se (comb-n se 2 0.025 0.9)
               se (normalizer (g-verb se :roomsize 190 :revtime 13 :taillevel 1 :drylevel 0.3))
               me (env-gen (envelope [0 1 1 0] [5 20 5]) 1 1 0 1)
               sem (* se m)
               hat-g (demand (impulse 7) 0 (dser [1 0 1 1 0 1 0 0 1 0 1 0 0 1 0 1 0 0] 9000))
               hat-c (demand (impulse 16) 0 (dser [1 0 0 0 1 0  1 0 0 1 0 1 0 1 0] 9000))
               hat (square 6000)
               hat (rlpf hat (env-gen (perc 0 0.01 1 -6) hat-g 5000 2000 1) 0.9)

               hvar (demand (impulse 3) 0 (drand [0.25 0.5 0.1 1] 9000))
               hvar2 (demand (impulse 6) 0 (drand [0.25 0.5 0.1 1] 9000))
               hat-b (* (freq-shift  (comb-n hat 6 (lpf hvar 200) 4) 120) hat (decay hat-c 0.81))
               hat-a (* (freq-shift  (comb-n hat 6 (lpf hvar2 200) 3) -60) hat (decay hat-g 0.81))
               ]
           (out 0 (pan2 (* me  hat-a) (lf-noise2 2)))
           (out 0 (pan2 (* me  hat-b) (lf-noise2 2)))))
(stop)

(demo 30 (let [me (env-gen (envelope [0 1 1 0] [5 20 5]) 1 1 0 1)
               g (demand (impulse 8) 0 (dser [1 0 1 0 1 0 0 1 0 1 0 0] 9000))
               m (* 2 (demand (impulse 5) 0 (drand [30 31 37 39 36 35] 9000)))
               s (freq-shift (sin-osc-fb (env-gen (perc 0 0.01 1 10) g (midicps m) 20 1) 3) 8)
               a (env-gen (perc 0 0.13 1 0) g 1 0)
               sa (* s a)
               sa (distort (* 80 (resonz sa 1280 0.9)))
               sa (g-verb sa :revtime 9 :roomsize 105 :spread 0.17 :drylevel 0.7 :taillevel 0.3)
               ]
           (out 0 (pan2 (* me sa 0.35) (lf-noise2 2)))
           ))



(dotimes [n 10 ]  (demo 30 (let [me (env-gen (envelope [0 1 1 0] [5 20 5]) 1 1 0 1)
                                 me2 (env-gen (envelope [0 1 1 0] [5 10 5]) 1 1 0 1)
                                  s (saw (+ (env-gen (perc 0 (* 20 (choose [1 2 6 8 3 4 18])) 1 0) 1
                                                    (choose [350 340 670 230 -300 -8000 -400 900])
                                                    (choose [0.1 0.13 -0.1 0 0.03]))
                                           (midicps (choose [30 34 38 42 46 50 54 58 70 86 90 74 68 58 93]))
                                           (lin-exp (lf-tri (choose [ 3 5 2 1/3 2/3 6 9])) 0 1 0 20)))
                                 s (resonz s (lin-exp (lf-tri (choose [ 3 5 2 1/3 2/3 6 9])) 0 1 1300 5000) 0.94)
                                 s (distort (* (choose [1 2 5 7 9])
                                               s))
                                 s (* s me2)
                                 s (free-verb s 0.7 0.9 0.8)

                                 ]
                             (out 0 (pan2 (* me s) (lf-noise2 0.4))))))
(stop)





(def m1 (metronome 3))
(metro-bpm m1 120)
(def g (ref [1]))
(definst k
  [f 200]
  (let [e (env-gen (perc 0 0.8 1 0) 1 1 0 FREE)
        e2 (env-gen (perc 0 0.2 1 0) 1 1 0 )
        fe (env-gen (perc 0.025 0.3 1 0) 1 301 f )
        fe2 (env-gen (perc 0.025 0.3 1 0) 1 -2000 f )
        s (saw (+ fe  (lin-exp (lf-noise0 40) -1 1 0 200)))
        s (* s (distort (* 30 (* e (formant:ar (* 0.2 fe) 200 0.9)))))
        s (+ s (* e2 (square fe2)))
        s (freq-shift s (lin-lin (lf-noise2 3) -1 1 0 (lin-exp (lf-tri (choose [1 16 7 3 40 600])) 0 1 0 40)))
        se (* s e)
        se (resonz se (lin-exp (lf-noise2 20) -1 1 300 900) 0.3)
      ;  se (g-verb se :roomsize 9 :revtime 3.19)
        se (comb-c se 1 (lin-lin (lf-noise2 40) -1 1 0.1 0.9) 2.9)
        se (freq-shift se -200)
        se (normalizer se)
;        h (white-noise)
 ;       h (* h (decay (impulse 2) 0.01))
       ; se2 (+ se )
        ;sil (detect-silence se 0.01 0.1 FREE)
        ]
    (out 0 (pan2 se 0 0.6))))
(k)

(stop)
(defn kp []
  (dotimes [n 1]
    (k :f (* 10 (midi->hz (choose [130 79 70 76 96 59]))))))

(kp)
(metro-bpm m1 60)
(at-zero-beat m1 1 @g g #'kp)
(stop)

(dosync (ref-set g [1 0 1 0 0 1 0 1 0 0 1 0 1 0 0 1 0 1 1 0 1 0]))


(definst k []
  (let [e (env-gen (envelope [0 1 1 0] [10 30 10]) 1 1 0 FREE)
        feed-in (local-in)
        s (normalizer  (resonz feed-in (lin-exp (lf-noise2 3) -1 1 200 300) 0.093)
                      )
        s2  (g-verb s
                   :roomsize 20
                   :revtime 2
                   :drylevel 0.9
                   :taillevel 0.2
                   :earlyreflevel 0.1)
        se (* (mix [s (* 0.013 s2)]) s e)
        o (* 0.1 (mix [(sin-osc 50) (white-noise)]))
        ]
    (local-out (+ s o))
    (out 0 (pan2 se (lf-noise2 2) 1))))
(k)
(stop)
