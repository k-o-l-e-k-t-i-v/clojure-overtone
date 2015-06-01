;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone

(boot-external-server)
;(demo 100 (* 1 (sin-osc 17500)))
(stop)







(demo 100 (* (env-gen (envelope [0 1 1 0] [10 80 10]) 1 1 0 1)
             (decay (impulse:kr 1) 0.62)
             (sin-osc (+ 100 (* 100 (sin-osc 900) (lf-noise2:ar 2))))))

(stop)
(dotimes [n 60]
  (demo 100 (* (env-gen (envelope [0 0.4 0.4 0] [10 80 10]) 1 1 0 1)
               (decay (impulse:ar (* (rand 1) (lf-noise2:ar 1))) 0.062)
               (sin-osc (+ (+ 20 (rand 10)) (* 100 (sin-osc 900) (lf-noise2:ar 2)))))))


(demo 15 (* (env-gen (envelope [ 0 0.7 0] [5 5]))
            (free-verb (normalizer (resonz (square (lin-exp (sin-osc (* 30 (lf-noise2:ar 4))) -1 1 70 80)) (lin-exp (lf-tri 12) -1 1 50 3000) 1.9)) 0.28 0.8 0.2)))



(definst x
  [freq 50]
  (let [aenv (env-gen (perc 0 3 -10) 1 0.41 0 1 FREE)
        fenv (env-gen (perc 0 0.01) 1 2000 freq 1)
        ffenv (env-gen (perc 0 0.2) 1 30 60 1)
        src  (sin-osc-fb fenv 0.2)
        src  (freq-shift src ffenv)
        src  (distort (* 1 src))
        src  (free-verb src 0.092 0.99 0.9)
        src  (normalizer src)
        src  (* src aenv)]
    (out 0 src)))

(x)

(def t 1)
(def c true)

(defn bab
  []
  (let [tt (now)]
    (at tt (haba))
;    (apply-at (+ tt t) bab [])
    ))




(defn baba
  []
  (let [r (rand-int 4)]
    (cond (= r 2) (x :freq 10000)
          (= r 3) (x :freq 4)
          (= r 1) (x :freq 3)
          (= r 0) (x :freq 5))))

(defn haba
  []
  (let [r (rand-int 4)]
    (cond (= r 2) (hat :dur 1 :freq 7600)
          (= r 3) (hat :dur 0.4 :freq 8800)
          (= r 1) (hat :dur 0.23 :freq 7500)
          (= r 0) (hat :dur 0.05 :freq 7400))))

(bab)

(definst hat
  [d 0.2
   freq 8000
   q 0.2]
  (let [src  (lf-noise2:ar freq)
        aenv (env-gen (perc 0 d) 1 1 0 1 FREE)
        src  (bpf src (* 0.25 freq) q)
        src  (normalizer src )
        src  (freq-shift:ar src 3000)
        src  (* src aenv)]
    (out 0 src)))

(hat)
