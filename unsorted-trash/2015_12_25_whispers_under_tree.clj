(use 'late-night.core)
(use 'overtone.core)


(def b1 (buffer (* 2 44100)))
(println b1)




(demo 12 (out 0 (pan2
                 (* (decay (impulse 2) 0.4)
                    (sin-osc 120)
                    (buf-rd 1 b1 0 1 2)
                    ))))
(stop)


(demo 2 (out 0 (pan2 (play-buf 1 b1))))

(def p1 (ref [8 4 12 8 4 12 8 4 12 3 3 3]))

(dotimes [n 20]
  (at (+ (* 2.5 n )
         (now))

      (demo 13 (let [g (decay (impulse (demand (impulse (a2k (ceil (lin-lin (lf-noise0 4) -1 1 1 16)))) 0 (dseq @p1 INF))) 0.14)
                     s (buf-rd:ar 1 b1 (phasor:ar :rate (+ (demand (demand (impulse (a2k
                                                                                     (ceil
                                                                                      (lin-lin
                                                                                       (lf-noise0 42) -1 1 1 16))))
                                                                           0
                                                                           (dseq @p1 INF))
                                                                   0
                                                                   (dseq @p1 INF))
                                                           (lin-exp (lf-noise2 1/12)
                                                                    -1 1 2 30))
                                                  :start 0
                                                  :end (* 2 44100)) 1 0)
                     s (* g s)
                     s (freq-shift s (* 100 (demand (impulse 4) 0 (dseq @p1 INF))))
                     s (distort (* 4 s))
                     s (free-verb s 0.15 0.49 0.2)
                     s (* 0.3 s)
                     m (env-gen (envelope [ 0 1 1 0] [2 6 2]) 1 1 0 1 FREE)
                     s (* s m)
                     s (resonz s (lin-lin (lf-noise2 3) -1 1 3000 12000) 0.9)
                     s (free-verb s 0.892 0.7 0.9)
                     ]
                 (out 0 (pan2 s (lf-noise2 3) 0.153))))))
(stop)
(doc phasor:ar)

(doc buf-rd:ar)
(def in-bus (audio-bus))

(record-buf (in:ar 8) b1 )

(demo 2 (out 0 (pan2 (in:ar 0 1))))

(demo 2 (out 0 (pan2 (in:ar 8))))
(demo 2 (record-buf (in:ar 8) b1))

(:ar )



(demo 17 (let [g (decay (impulse (demand (impulse (a2k (ceil (lin-lin (lf-noise0 4) -1 1 1 16)))) 0 (dseq @p1 INF))) 0.04)
                     s (buf-rd:ar 1 b1 (phasor:ar :rate (+ (demand (demand (impulse (a2k
                                                                                     (ceil
                                                                                      (lin-lin
                                                                                       (lf-noise0 42) -1 1 1 16))))
                                                                           0
                                                                           (dseq @p1 INF))
                                                                   0
                                                                   (dseq @p1 INF))
                                                           (lin-exp (lf-noise2 1/12)
                                                                    -1 1 2 30))
                                                  :start 0
                                                  :end (* 2 44100)) 1 0)
                     s (* g s)
                     s (freq-shift s (* 1000 (demand (impulse 4) 0 (dseq @p1 INF))))
                     s (distort (* 4 s))
                     s (free-verb s 0.15 0.49 0.2)
                     s (* 0.3 s)
                     m (env-gen (envelope [ 0 1 1 0] [2 6 2]) 1 1 0 1 FREE)
 ;
               s3 (comb-n s 2 (lin-lin (* (lf-noise2 4)
                                          (lf-noise2 6)) -1 1 0.001 0.006) 2)
               s2 (rlpf s3 (lin-lin (* (lf-noise2 16)
                                         (lf-noise2 19)
                                         (lf-noise2 6)) -1 1 30 8000)
                          (lin-exp (* (lf-noise2 6)
                                      (lf-noise2 2)) -1 1 0.02 0.99))
               s2 (free-verb s2 0.92 0.97 0.9)
               s (mix [(* 0.5 s2 (lf-noise2 4))
 ;                      (* s (lf-noise2 6))
;                       (* 0.1 (normalizer s2 0.9))
                       ])
               s (* s m)
                     ]
           (out 0 (pan2 s (* 0.3 (lf-noise2 3)) 0.153))))
