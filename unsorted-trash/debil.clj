;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone

(definst vingl
  [note     60
   peak     1
   sustain  0.9
   a        0.7
   d        0.63
   r        0.3
   dur      0.4
   facc     20
   fcrv     2
   fs       2
   fltlo    40
   flthi    2000
   qs       10
   qlo      0.27
   qhi      2.3
   verbmix  0.395
   verbsiz  0.96
   verbdmp  0.93
   ps       0.3]
  (let [freq (midicps note)
        aenv (env-gen (envelope [0 peak sustain sustain 0] [a d dur r]) 1 1 0 1)
        fenv (env-gen (perc 0 a facc fcrv) 1 1 freq 1)
        fch  (lin-exp (lf-noise2 fs) -1 1 fltlo flthi)
        qch  (apply + (lin-exp (lf-noise2 (* qs [1.01 2.3 4.03])) -1 1 qlo qhi))
        src  (apply + (saw (* fenv [1.003 2.001 2.9997 4.0999])))
        flt1 (rlpf src fch qch)
        flt2 (apply + (bpf src (* [0.25 0.5 1.09 2.13] fch) (* [1 0.2 0.3] qch)))
        src  (+ flt1 flt2)
        src  (normalizer src)
        src  (* aenv src)
        src  (free-verb src verbmix verbsiz verbdmp)
        sil  (detect-silence src 0.0001 3 FREE)
        pant (* 0.5 (lf-noise2:ar ps))
        ]
    (out 0 (pan2 src pant 1))))

(definst dist-boom
  [freq 30
   facc 500
   amp  0.5
   dacc 200
   ddur 0.52
   adur 0.3
   fdur 0.03
   fcur -10
   acur -10
   dela 0.02]
  (let [fenv (env-gen (perc 0 fdur fcur) 1 facc freq 1)
        aenv (env-gen (perc 0 adur acur) 1 amp 0 1 FREE)
        denv (env-gen (perc 0 ddur) 1 dacc 0 1)
        src  (sin-osc fenv)
        src  (distort (* denv src))
        src  (normalizer src)
        src  (* aenv src)
        src  (allpass-c:ar src 1 dela 0.25)
        src  (free-verb src 0.13 0.06 0.05)]
    (out 0 (pan2 src (* 0.4 (lf-noise2:ar 3)) 1))))

(dist-boom :ddur 0.2 :facc 20)


(dist-boom :adur 2 :freq (midi->hz (note :c3)) :facc 0 :dacc 1)


(defn meka
  [n]
  (cond (= n 0) (vingl :dur 0.1 :r 2.5 :note (midi->hz (note :c1)))
        (= n 14) (vingl :a 0.01 :dur 0.1 :r 0.15 :note (midi->hz (note :d1)))
        (= n 20) (vingl :a 1.91 :dur 0.1 :r 0.15 :note (midi->hz (note :g1)) :amp 0.1 :facc 1)
        (= n 38) (vingl :a 0.41 :dur 0.31 :R 0.15 :note (midi->hz (note :g#0)))
        (= n 46) (vingl :a 0.41 :dur 0.31 :R 0.15 :note (midi->hz (note :a#0)))))

(meka 0)
(node-tree)
(bom 3)

(defn bata
  [n]
  (cond (= (mod n 4) 0) (dist-boom :ddur 0.2 :facc 20 :dacc 17 :dela (rand 0.01))
        (= (mod n 7) 0) (dist-boom :ddur 0.02 :facc 200 :dacc 6 :dela (rand 0.1))
        (= (mod n 5) 0) (dist-boom :ddur 0.2 :facc 1000 :dacc 6 :dela (rand 1))))


(def runing? (= 1 0))

runing?


(defn metr
  [metro t n m]
  (let [beatn (metro)
        t1 (metro beatn)
        t2 (metro (+ beatn t))
        nn (mod (+ n 1) m)]
    (at t1 (do ;(bata n)
               (meka n)))
    (if runing?
      (apply-at t2 metr metro t nn m []))))

(metr m1 1 0 64)
(stop)

(def m1 (metronome 230))
(metro-bpm m1 600)

(m1)

(let [b (m1)
      stamp1 (m1 b)
      stamp2 (m1 (+ b 2))]
  (println (str "beat number: " b " \nb stamp: " stamp1 "\nc stamp: " stamp2)))



(m1)
