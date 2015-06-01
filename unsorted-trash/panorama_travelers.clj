;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone


(taran)

(definst taran
  [freq 90
   fmod 10
   t1 15
   t2 15
   t3 15]
  (out 0
        (pan4 (free-verb (* (decay2 (dust 2) 0.3 0.623)
                          (env-gen (envelope [0 1 1 0]
                                             [t1 t2 t3])
                                   1
                                   1
                                   0
                                   1
                                   FREE)
                          (normalizer (distort (* (* 2000
                                                     (lf-noise2:ar (rand 1)))
                                                  (resonz (distort (* (lin-exp (lf-noise2:ar (rand 4))
                                                                               -1
                                                                               1
                                                                               1
                                                                               6000)
                                                                      (sin-osc-fb (+ freq
                                                                                     (* fmod
                                                                                        (lf-noise2:ar (rand 0.14))))
                                                                                  (+ 0.5 (* 0.5
                                                                                            (lf-noise2:ar (rand 10)))))))
                                                          (lin-exp (lf-noise2:ar (rand 4))
                                                                   -1
                                                                   1
                                                                   50
                                                                   2000)
                                                          9.9)))))
                         0.243
                         0.98
                         0.32)
              (lf-noise2:ar 0.4)
              (lf-noise2:ar 0.4)
              0.5)))
(taran 300 200)
(def continue? 1)
(str continue?)
(defn taran-metro
  [t]
  (let [n (now)
        c (pos? continue?)]
    (at n (taran))
    (while c
      (apply-at (+ n t) taran-metro t []))))

(pos? continue?)
(taran-metro)
(dotimes [n 30]
  ;(Thread/sleep 10000)
  (taran 80 9 10 120 30))

(stop)
(taran)

(definst rotator
  [rate 1
   freq 80
   a    20
   r    20]
  (let [aenv (env-gen (envelope [0 1 0] [a r]) 1 1 0 1 FREE)
        src  (sin-osc-fb freq 0.3)
        src  (* src aenv)
        lr   (distort (* 10 (sin-osc (/ rate 4) 0.25)))
        fb   (distort (* 10 (sin-osc (/ rate 4) 0)))]
    (out 0 (pan4 src lr fb 0.5))))

(definst rotator2
  [rate 1
   freq 80
   lr   0
   fb   0]
  (let [aenv (decay (impulse:kr rate) 0.4)
        src  (sin-osc-fb freq 0.3)
        src  (* src aenv)]
    (out 0 (pan4 src lr fb 1))))

(definst mouse-rotator
  [rate 1
   freq 80
   a    20
   r    20]
  (let [;aenv (env-gen (envelope [0 1 0] [a r]) 1 1 0 1 FREE)
        aenv (decay (impulse:kr rate) 0.3)
        src  (sin-osc-fb freq 0.3)
        src  (* src aenv)
        lr   (mouse-x:kr 1 -1)
        fb   (mouse-y:kr -1 1)]
    (out 0 (pan4 src lr fb 0.5))))

(rotator2 9 650 1 1)
(mouse-rotator 1.16 500)
(kill mouse-rotator)
(kill taran)
(stop)

(demo 10
      (let [x (mouse-x:kr 20 200)
            y (mouse-y:kr 20 200)
            sik (sin-osc 200)]
        (pan2 sik 0 1)))
