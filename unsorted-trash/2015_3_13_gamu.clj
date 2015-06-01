;; Version 1.0beta26
;;
;;           http://github.com/overtone/emacs-live
;;
;;


(demo 100 (out 0 (pan2 (saw 8))))
(stop)



(def c1 (control-bus 1))
(def c2 (control-bus 1))
(definst cs
  [freq 1
   outb 0]
  (let [src (impulse:kr freq)]
    (out outb src)))

(do
  (def cs1 (cs :freq 4 :outb c1))
  (def cs2 (cs :freq 3 :outb c1))
  (def cs3 (cs :freq 1/3 :outb c1)))

(def cs4 (cs :freq 4/3 :outb c1))
(do
  (def cs5 (cs :freq 6/10 :outb c2))
  (def cs6 (cs :freq 7/16 :outb c2)))
(ctl cs2 :freq 2/8)
(ctl cs1 :freq 1/4)
(ctl cs3 :freq 1/7)

(ctl cs1 :freq 2)
(ctl cs2 :freq 8)
(ctl cs3 :freq 5)
(ctl bk1 :fb 20)
(ctl bk1 :fm 3000)
(ctl bk1 :a 0.4)


(stop)
(kill cs3)
(node-tree)

(definst bk
  [fb 40
   fm 1000
   ft 0.01
   a  1
   at 0.3
   cbus 0]
  (let [t (in:kr cbus)
        env-amp (env-gen (perc 0 at a 0) t 1 0 1)
        env-freq (env-gen (perc 0 ft fm 0) t 1 fb 1)
        s (sin-osc-fb env-freq 0.4)
        s (* s env-amp)
        ]
    (out 0 (pan2 s 0))))

(def bk1 (bk :cbus c1))
(def bk2 (bk :cbus c2 :freq 1000))
(def bk3 (bk :cbus c2 :fb 500))
(ctl bk1 :fb 80)
(ctl bk2 :freq 1000)
(kill bk3)
(kill bk1)
(stop)
(demo 1 (out 0 (pan2 (sin-osc 200))))

(ctl bk1 :fm 900 :at 0.5)

( demo 100 (replace-out [0 1] (free-verb2 (in:ar 0) (in:ar 1) 0.3 0.9 0.9) ))



(stop)

(dotimes [n 50]
  (demo 220 (out 0 (pan2 (* (env-gen (envelope [0 1 1 0] [10 200 10]))
                           (saw (rand 5))
                           (normalizer (free-verb (* (lf-noise2 2)
                                                    (resonz (square (+ 50 (* 5 (lf-noise2 (rand 30))))) (+ 4000 (* 3800 (lf-noise2 (rand 10)))) 0.9)
                                                    )
                                                  0.3 0.97 0.93) 1)) 0 0.2))))

(dotimes [n 50]
  (demo 22 (out 0 (pan2 (* (env-gen (envelope [0 1 1 0] [1 20 1]))
                           (saw (rand 5))
                           (normalizer (free-verb (* (lf-noise2 2)
                                                    (resonz (square (+ 50 (* 5 (lf-noise2 (rand 30))))) (+ 4000 (* 3800 (lf-noise2 3))) 0.9)
                                                    )
                                                  0.3 0.97 0.93) 1)) 0 0.2))))


(boot-external-server)
