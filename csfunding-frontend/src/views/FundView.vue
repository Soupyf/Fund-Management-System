<template>
    <div>

        <div class="SelectForm">
            <div style="height: 10px"></div>
            <el-form :inline="true" :model="SearchForm" class="demo-form-inline">
                <el-form-item label="编号">
                    <el-input v-model="SearchForm.FundNumber" placeholder="经费编号"/>
                </el-form-item>
                <el-form-item label="名称">
                    <el-input v-model="SearchForm.FundName" placeholder="经费名称"/>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="onSubmit">查询</el-button>
                </el-form-item>
            </el-form>
        </div>


        <el-table :data="tableData" style="width: 100%" border class="ParentTable" :row-class-name="tableRowClassName"
                  :summary-method="getSummaries" show-summary>

            <el-table-column prop="id" label="经费编号" align="center"/>
            <el-table-column prop="name" label="经费名称" align="center"/>
            <el-table-column prop="sum" label="经费总额" align="center"/>
            <el-table-column prop="remain" label="经费剩余" align="center"/>
            <el-table-column prop="start" label="开始时间" align="center"/>
            <el-table-column prop="end" label="结束时间" align="center"/>
            <el-table-column prop="state" label="是否达标" align="center">
                <template #default="props">
                    <el-tag type="error" size="small" plain v-if="props.row.remainDay < 60
                                                 && props.row.remain/props.row.sum > 0.2" effect="dark">
                        不合格
                    </el-tag>

                    <el-tag type="success" size="small" plain v-if="!(props.row.remainDay < 60
                                                 && props.row.remain/props.row.sum > 0.2)" effect="dark">
                        合格
                    </el-tag>

                </template>
            </el-table-column>
          <el-table-column prop="operation" label="操作" align="center">
            <template #default="props">
              <el-button @click="BarChartVisible = !BarChartVisible" type="primary" size="small" plain>
                {{ BarChartVisible ? '返回' : '月支出' }}
              </el-button>
              <el-button  type="primary" size="small" plain @click="RemainDay = props.row.remainDay;
                                                                  Rate = props.row.remain/props.row.sum;
                                                              UsedAmount = props.row.sum -
                                                              props.row.remain;InfoVisible = true">
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="Pagination">
            <el-pagination background layout="prev, pager, next" :total="1000"/>
        </div>


        <div class="Bottom">
            <div style="height: 5px"></div>
            <el-button style="background-color: #8f000b; border: #8f000b">
                <el-icon style="vertical-align: middle;" size="25px">
                    <Back/>
                </el-icon>
                <font style="color: white">返回主页</font>
            </el-button>
        </div>

      <div class = "BarChart"
           ref="chartContainer" :style="{ width: '800px', height: '500px',
       display: BarChartVisible ? 'block' : 'none' }" >
      </div>

      <el-dialog v-model="InfoVisible" title="详细信息" width="30%" draggable >
          <h4>已使用经费: {{UsedAmount}}</h4>
          <h4>当前执行率: {{Rate}}</h4>
          <h4>剩余时间: {{RemainDay}}</h4>
        <template #footer>
    <span class="dialog-footer" style="height: 20px">
      <el-button type="danger" @click="InfoVisible = false;">
        取消
      </el-button>
    </span>
        </template>
      </el-dialog>

    </div>
</template>

<script lang="ts" setup>

import {onMounted, reactive} from "vue";
import type {TableColumnCtx} from 'element-plus'
import {
    Delete, Edit, Search, Share, Upload, Bell, Back,
    UploadFilled, Document, View, Coin, Notification, HomeFilled
} from '@element-plus/icons-vue'
import request from "@/util/request";
import {useRoute} from "vue-router";
import moment from "moment"


import { ref } from 'vue';
import * as echarts from 'echarts/core';
import { BarChart } from 'echarts/charts';
import { TitleComponent, TooltipComponent, GridComponent } from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';

echarts.use([TitleComponent, TooltipComponent, GridComponent, BarChart, CanvasRenderer]);

const chartContainer = ref<HTMLElement | null>(null);

let BarChartVisible = ref(false)
let InfoVisible = ref(false)
let RemainDay = ref(0)
let Rate = ref(0)
let UsedAmount = ref(0)

onMounted(() => {
  if (chartContainer.value) {
    const chart = echarts.init(chartContainer.value);

    // Prepare data for the chart
    const chartData = [
      { name: '1', value: 100 },
      { name: '2', value: 200 },
      { name: '3', value: 150 },
      { name: '4', value: 100 },
      { name: '5', value: 200 },
      { name: '6', value: 150 },
      { name: '7', value: 100 },
      { name: '8', value: 200 },
      { name: '9', value: 150 },
      { name: '10', value: 100 },
      { name: '11', value: 200 },
      { name: '12', value: 150 }
    ];

    // Configure the chart options
    const options = {
      title: {
        text: '各月支出',
      },
      tooltip: {},
      xAxis: {
        type: 'category',
        data: chartData.map((data) => data.name),
      },
      yAxis: {
        type: 'value',
      },
      series: [
        {
          type: 'bar',
          data: chartData.map((data) => data.value),
        },
      ],
    };

    // Set the options and render the chart
    chart.setOption(options);
  }
});

defineExpose({
  chartContainer,
});

const route = useRoute()


const tableData = reactive<fund[]>([])

function getFundData(fid: any) {
    request({
        url: `/get-fund?uuid=${fid}`,
        method: 'get',
    }).then(res => {
        let rd = res.data.data;
        let fd: fund = {
            id: rd.uuid,
            name: rd.fundName,
            sum: rd.sum,
            remain: rd.balance,
            start: moment.unix(rd.startTime / 1000).format('YYYY-MM-DD'),
            end: moment.unix(rd.endTime / 1000).format('YYYY-MM-DD'),
            remainDay: rd.remainDays,
            applies: []
        };
        tableData.push(fd)
    })
}

function getTableData(gid: any) {
    request({
        url: `/fund-research-group?gid=${gid}`,
        method: 'get'
    }).then(res => {
        let groups = res.data.data;
        groups.forEach((item: any) => {
            getFundData(item)
        })
    })
}

onMounted(() => {
    getTableData(route.query.gid);
    // getFundData(route.query.gid);
})

const SearchForm = reactive({
    FundNumber: '',
    FundName: '',
})

interface fund {
    id: string,
    name: string,
    sum: number,
    remain: number,
    start: string,
    end: string,
    remainDay: number,
    applies: apply[]
}

interface apply {
    name: string
    researchGroup: string
    applyPerson: string
    amount: number
    state: string
}

const tableRowClassName = (
    {row, rowIndex,}:
        {
            row: fund
            rowIndex: number
        }) => {
    if (row.remainDay < 60 && row.remain / row.sum > 0.2) {
        return 'warning-row'
    }
    return 'success-row'
}

interface SummaryMethodProps<T = fund> {
    columns: TableColumnCtx<T>[]
    data: T[]
}

const getSummaries = (param: SummaryMethodProps) => {
    const {columns, data} = param
    const sums: string[] = []
    columns.forEach((column, index) => {
        if (index === 0) {
            sums[index] = '总'
            return
        }
        const values = data.map((item) => Number(item[column.property]))
        if (!values.every((value) => Number.isNaN(value))) {
            sums[index] = ` ${values.reduce((prev, curr) => {
                const value = Number(curr)
                if (!Number.isNaN(value)) {
                    return prev + curr
                } else {
                    return prev
                }
            }, 0)}`
        } else {
            sums[index] = '/'
        }
    })

    return sums
}


</script>

<style>
.BarChart {
  position:absolute;
  /* 水平垂直居中 */
  top: 30%;
  left: 23%;


  width: 100vw;
  height: 100vh;
  background-color: rgba(100, 100, 100, 0.7);
  z-index: 9999;
}
</style>