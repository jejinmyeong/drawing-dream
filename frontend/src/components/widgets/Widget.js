import React from "react";
import styled from "styled-components";
import { motion } from "framer-motion";

const Container = styled(motion.div)`
  height: 5rem;
  box-shadow: rgba(99, 99, 99, 0.2) 0px 2px 8px 0px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  margin: 1rem;
`;

const Name = styled.div`
  font-weight: 600;
`;
const Widget = ({ name, id, setWidgetId }) => {
  return (
    <Container
      layout
      layoutId={id}
      whileHover={{
        scale: 1.02,
      }}
      onClick={() => {
        setWidgetId(id);
      }}
    >
      <Name>{name}</Name>
    </Container>
  );
};

export default Widget;