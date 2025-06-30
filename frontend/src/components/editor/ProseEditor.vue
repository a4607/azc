/* eslint-disable no-unused-vars */
<template>
  <editor-content :editor="editor" />
</template>

<script setup>
import { EditorContent } from '@tiptap/vue-3'
import { defineProps } from 'vue'

defineProps({
  editor: {
    type: Object,
    required: true,
  },
})
</script>

<style lang="scss">
.ProseMirror {
  outline: none;
  min-height: calc(100vh - 200px); // Adjust based on toolbar/header height
}

.ProseMirror p.is-editor-empty:first-child::before {
  content: attr(data-placeholder);
  float: left;
  color: #adb5bd;
  pointer-events: none;
  height: 0;
}

/* Basic image styling */
.ProseMirror img {
  max-width: 100%;
  height: auto;

  &.ProseMirror-selectednode {
    outline: 2px solid #68CEF8;
  }
}

/* Syntax Highlighting */
pre {
  background: #0D0D0D;
  color: #FFF;
  font-family: 'JetBrainsMono', monospace;
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;

  code {
    color: inherit;
    padding: 0;
    background: none;
    font-size: 0.8rem;
  }

  .hljs-comment,
  .hljs-quote {
    color: #616161;
  }

  .hljs-variable,
  .hljs-template-variable,
  .hljs-attribute,
  .hljs-tag,
  .hljs-name,
  .hljs-regexp,
  .hljs-link,
  .hljs-name,
  .hljs-selector-id,
  .hljs-selector-class {
    color: #F98181;
  }

  .hljs-number,
  .hljs-meta,
  .hljs-built_in,
  .hljs-builtin-name,
  .hljs-literal,
  .hljs-type,
  .hljs-params {
    color: #FBBC88;
  }

  .hljs-string,
  .hljs-symbol,
  .hljs-bullet {
    color: #B9F18D;
  }

  .hljs-title,
  .hljs-section {
    color: #FAF594;
  }

  .hljs-keyword,
  .hljs-selector-tag {
    color: #70CFF8;
  }

  .hljs-emphasis {
    font-style: italic;
  }

  .hljs-strong {
    font-weight: 700;
  }
}
</style>