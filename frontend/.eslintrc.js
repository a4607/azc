module.exports = {
  root: true,
  env: {
    node: true,
    'vue/setup-compiler-macros': true
  },
  extends: [
    'plugin:vue/vue3-essential',
    'eslint:recommended'
  ],
  parserOptions: {
    parser: '@babel/eslint-parser'
  },
  rules: {
    'no-console': import.meta.env.PROD ? 'warn' : 'off',
    'no-debugger': import.meta.env.PROD ? 'warn' : 'off',
    'space-before-function-paren': 'off',
    'no-trailing-spaces': 'off',
    'eol-last': 'off',
    'quote-props': 'off',
    'dot-notation': 'off',
    'vue/multi-word-component-names': 'off',
    'vue/no-v-model-argument': 'off'
  }
}
