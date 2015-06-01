;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone
(definst boom [freq 40 acc 3000 t 0.01 t2 0.3 amp 1 nt 0.21 namp 0.13 nfreq 200 nacc 6000 nft 0.03]
  (let [freq-env (line:kr acc freq t)
        noise-freq-env (line:kr nacc nfreq nft)
        amp-env (line:kr amp 0 t2)
        sig (sin-osc freq-env)
        noise (* 10 (rlpf (* (line:kr namp 0 nt) (lf-noise1 freq-env)) noise-freq-env 0.153))
        drum (* amp-env (+ sig noise))
        verb (free-verb drum 0.337 0.27 0.92)
        silence (detect-silence verb 0.001 0.2 FREE)]
    (out [0 1] verb)))

(definst hat [freq 4000 acc 3000 t 0.01 t2 0.3 amp 1 nt 0.21 namp 0.13 nfreq 200 nacc 6000 nft 0.03]
  (let [freq-env (line:kr acc freq t)
        noise-freq-env (line:kr nacc nfreq nft)
        amp-env (line:kr amp 0 t2)
        sig (* 0.1 (saw freq-env))
        noise (* 10 (rhpf (* (line:kr namp 0 nt) (lf-noise1 freq-env)) noise-freq-env 0.153))
        drum (* amp-env (+ sig noise))
        verb (free-verb drum 0.9337 0.927 0.92)
        silence (detect-silence verb 0.001 0.2 FREE)]
    (out [0 1] verb)))
(hat)
(boom :acc 500 :nfreq 4000)
(def metro (metronome 230))
(defn pattern-1 [sound beat t n]
  (at (metro beat) (eval sound))
  (if (pos? (- n 1))
    (apply-by (metro (+ t beat)) #'pattern-1 sound (+ t beat) t (dec n) []))
  )

(pattern-1 '(boom :acc 500 :nfreq 2000) (metro) 1 16)
(pattern-1 (metro) 9)
(pattern-1 (metro) 5)
(metro :bpm 640)
(clearx-all)

(stop)
