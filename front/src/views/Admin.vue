<script setup>
import { ref } from 'vue';
import { ElTable, ElTableColumn, ElButton } from 'element-plus';

const users = ref([
  { id: 1, name: '用户1', email: 'user1@example.com' },
  { id: 2, name: '用户2', email: 'user2@example.com' },
]);
</script>

<template>
  <div>
    <h1>管理员界面</h1>
    <ElTable :data="users">
      <ElTableColumn prop="id" label="ID" />
      <ElTableColumn prop="name" label="姓名" />
      <ElTableColumn prop="email" label="邮箱" />
    </ElTable>
  </div>
</template>

<style scoped>
/* 可根据前端设计.pdf 美化样式 */
</style>