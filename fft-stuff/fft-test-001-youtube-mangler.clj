(use 'overtone.core)
(use 'late-night.core)


(def buf-a (buffer (* 2 1024) 1))
(def buf-b (buffer (* 4 1024) 1))

(def buf-c (buffer (* 4 1024) 1))
(def buf-d (buffer (* 4 1024) 1))

(stop)
(demo 60 (let [;x (white-noise:ar)
               x (in:ar 8 1)
;               x (distort (* 4 x))
 ;              x (freq-shift x -200 (lf-noise2 4))
               xfft (fft buf-c x)
               c (pv-copy xfft buf-d)
               xpv (pv-mag-freeze xfft (lf-noise0:kr 1))
                                        ;xpv (pv-mag-gate xpv 0.9 0.1)
               ;c
               xpv2 (pv-mag-shift c
                                  (lin-lin (lf-noise2:kr 1) -1 1 0.75 1.24)
                                  (lin-lin (lf-noise2:kr 1) -1 1 0 3))
               xpv (pv-diffuser xpv (impulse:kr 1/2))
;               xpv (pv-rect-comb xpv 1 (impulse 1) 0.629)

               ;xpv (pv-morph xpv xpv2 (lin-lin:kr (lf-noise2:kr 2) -1 1 0 1))
               xpv (pv-morph:kr xpv xpv2 (a2k (lin-lin (lf-noise2 2) -1 1 0 1)))
                                        ;xpv (pv-soft-wipe xpv xpv2 (phasor (impulse 1) 2 0 1 1))
               xpv (pv-mul xpv xpv2)
               ;; xpv (pv-bin-shift xpv (a2k
               ;;                        (lin-exp
               ;;                         (lf-noise0 (lin-lin (lf-noise2 4)
               ;;                                             -1 1 0.2 1.5))
               ;;                         -1 1 0.2 4)) 20)
               xpv (pv-mag-squared xpv)
               xpv (pv-mag-squared xpv)
;               xpv (pv-mag-squared xpv)
;               xpv (pv-mag-squared xpv)
               o (ifft xpv)
               o (normalizer o 0.75)
               o (distort (* 3 o))
               o (g-verb  o :roomsize 180 :revtime 3.4 :drylevel 0.9 :taillevel 0.5)
               o (normalizer o 0.75)
               o (* 0.5 o)
;               r (demand (impulse 1) 0 (drand [1 2 1/2 1/4 2/3] INF))
;               o (* o (env-gen (perc 0.1 0.3 1 0) (impulse r) 1 0 1))
;               o (g-verb  o :roomsize 120 :revtime 0.4 :drylevel 0.9)
               ;; o (rlpf o (lin-exp (lf-noise2 8)
               ;;                    -1 1
               ;;                    80 3000)
               ;;         0.96)
               ;; s (sin-osc (midicps (demand (impulse 1)
               ;;                             0
               ;;                             (drand [59 64 72 67 54 56] INF))))
               m (env-gen (envelope [0 1 1 0] [5 45 5]) 1 1 0 1)
               o (* o m)
               ]
           (out 0 (pan2 o))))
