;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone

(boot-external-server)

(dotimes [n 36]
    (demo 40
        (pan2 (freq-shift (free-verb  (freq-shift (* (env-gen (envelope [0 1 0]
                                                                        [15 15])
                                                              1
                                                              1
                                                              0
                                                              1)
                                                     (decay (impulse:kr n)
                                                            0.25)
                                                     (apply + (sin-osc (+ [(rand 200)
                                                                           (rand 200)
                                                                           (rand 200)
                                                                           (rand 200)]
                                                                          (* 20 n)))))
                                                  (lin-exp (lf-tri n) -1 1 0 200))
                                      0.39
                                      0.97
                                      0.19)
                          -200)
              (lin-exp (sin-osc (* 0.5) n) -1 1 -1 1 )
              0.3)))
(stop)
(kill-server)


(demo 10 (pan2 (sin-osc (+ 93 (* 30 (ball:ar (impulse:ar 0.1) 0.01831 0.159 0.172))))
                (sin-osc 0.498)))



;; (dotimes [n 21]
;;   (Thread/sleep 1000)
;;   (demo 1 (pan2 (free-verb  (* (decay (impulse:kr 0.01) 0.29)
;;                                (sin-osc (* n 100)))
;;                             0.6 0.98 0.2))))
(defsynth bomba
  [freq 30
   mixa 0.1
   dist 20
   amp 1.6]
  (let [aenv (env-gen (perc 0.01 0.2) 1 amp 0 1 FREE)
        fenv (env-gen (perc 0 0.04 1000) 1 1 freq 1)
        src (apply + (sin-osc (* [0.25 0.5 1 2] fenv)))
        src (clip:ar (* dist src) -1 1)
        src (rlpf:ar src fenv 0.92)
        src (normalizer src)
        src (* src aenv)
        src (free-verb src mixa 0.87 0.14)
        ]
    (out 0 (pan2 src 0 1))))

(def fs1 (freesound 34496))
(def fs2 (freesound 25126))
(def fs3 (freesound 13145))
(definst sopl
  [x 0]
  (let [s (buf-rd:ar x)
        s (normalizer s)]
    (out 0 (pan2 s (lf-noise2:ar 2) 1))))

(sample-player fs3 :amp 4 :rate 1)
;(sample)

                                        ;(sampl)


(stop)
(bomba :dist 20 :freq 120)

(defsynth melka
  [nota 40
   dur  2
   amp 0.3
   fbk  0.0]
  (let [aenv (env-gen (envelope [0 1 0.1 0] [0.1 0.7 0.3]) 1 1 0 dur FREE)
        src  (apply + (sin-osc-fb (* [1.5 2.031 3.07] (midicps nota)) fbk))
        src (* amp src aenv)]
    (out 0 (pan2 src (lf-noise2:ar 1) 1))))
(melka (note :c4) 10)
(node-tree)
(stop)

(defsynth ohat
  [flt 1000
   amp 0.2]
  (let [src  (rlpf (white-noise) flt)
        aenv (env-gen (perc 0 0.2) 1 amp 0 1 FREE)
        src (* aenv src)]
    (out 0 (pan2 src (lf-noise2:ar 1)))))




(map (melka :nota %) (chord :c4 :Cmajor))
                                        ;(chord  :Cmajor)
(postln (chord :c4 :major))

(stop)

(defn twobeat
  [n]
  (do (cond (= 0 (mod n 11)) (ohat 12000 0.2)
            (= 0 (mod n 3)) (ohat 1500 0.3)
            (= 0 (mod n 5)) (ohat 3000 0.4)
            (= 0 (mod n 8)) (ohat 7000 0.1))
      (cond (even? n) (do (bomba 32 0.15)
                          (cond (= 0 n) (do (sample-player fs3 :amp 2 :rate 0.15)
                                            (melka (- (note :d3) 0.07) 6 0.18))
                                (= 12 n) (melka (note :f3) 8)
                                (= 14 n) (melka (note :f4) 10 0.2 0.136)
                                (= 10 n) (melka (+ 0.1 (note :f3)) 3 0.2)
                                (or (= 24 n)
                                    (= 30 n)) (blupka (/ 200 1.5) 4 100 1.02)
                                (= 34 n) (blupka (/ 200 8) 8 80 0.97)))
             (= 1 n)   (do (bomba 200)
                           (melka (note :c4) 4)
                           (melka (note :f4) 3 0.2 0.2)
                           (melka (note :a#4) 2 0.1 0.4))
             (= 5 n)   (bomba 230 0.3)
             (= 11 n)  (bomba 240 0.8)
             (= 13 n)  (blupka (/ 200 3) 12 120 0.9047)
             (= 17 n)  (blupka (/ 200 7) 8 130 1.009))))

(defn blup
  [t n m]
  (let [modx (mod (inc n) m)
        tt   (now)]
    (at tt (twobeat modx))
    (apply-at (+ tt t ) blup [t modx m])
    ))


(defn blupka
  [t n f m]
  (let [tt (now)
        ff (* m f)]
    (when (pos? n)
      (do
        (at tt (bomba ff 0.2))
        (apply-at (+ tt t ) blupka [t (dec n) ff m])))))

(bomba)
(node-tree)
(blupka 50 8 1200 1.2)

(def b1 (blup 200 0 36))

                                        ;(stop b1)

(kill-server)
(boot-external-server)

(defsynth my-echo
  [bus 0 max-delay 1.0 delay-time 0.4 decay-time 2.0]
  (let [source (in bus)
        echo (comb-n source max-delay delay-time decay-time)]
    (replace-out bus (pan2 (+ echo source) 0))))



(stop)
