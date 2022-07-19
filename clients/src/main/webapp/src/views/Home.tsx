import React from "react"
import Sidebar from "../components/Sidebar";
import HeaderMenu from "../components/HeaderMenu";
import {NodeProvider} from "../contexts/NodeContext";
import {Outlet} from "react-router-dom";
import {Layout,} from "antd";

const {Header, Content, Sider} = Layout;


function Home() {
    return (
        <Layout className='h-screen'>
            <Header className='px-0'>
                <HeaderMenu/>
            </Header>
            <Layout>
                <Sider theme={'light'}>
                    <Sidebar/>
                </Sider>
                <Layout>
                    <Content className='px-5 pt-0.5 h-full'>
                        <Outlet/>
                    </Content>
                </Layout>
            </Layout>
        </Layout>
    )
}

export default Home
