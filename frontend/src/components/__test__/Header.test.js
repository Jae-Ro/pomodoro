// import React from 'react';
// import { render } from '@testing-library/react';
// import Header from '../Header';
// import ReactDOM from 'react-dom';

// import renderer from 'react-test-renderer'; 

// test('renders without crashing', () => {
//     const div = document.createElement("div");
//     ReactDOM.render(<Header></Header>, div)
// });

// it("matches snapshot", () =>{
//     const tree = renderer.create(<Header></Header>).toJSON();
//     expect(tree).toMatchSnapshot();
// })

import React from 'react';
import ReactDOM from 'react-dom';
import Header from '../Header';
import renderer from 'react-test-renderer';
import {shallow} from 'enzyme';
import toJSON from 'enzyme-to-json';
import { Provider } from "react-redux";
import configureMockStore from "redux-mock-store";

const mockStore = configureMockStore();
const store = mockStore({});


test("Header matches its snapshot",()=>{
    const wrapper = shallow(<Provider store={store}> <Header /> </Provider>)

    expect(toJSON(wrapper)).toMatchSnapshot();
})